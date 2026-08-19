import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { CURRENT_QUARTER } from '../../core/models/nomination.model';
import { NominationView, ReviewStatus } from '../../core/models/review.model';
import { AuthService } from '../../core/services/auth.service';
import { ReviewService } from '../../core/services/review.service';

/** Which rows the table is showing. */
export type ReviewFilter = ReviewStatus | 'ALL' | 'SHORTLIST';

interface FilterTab {
  readonly id: ReviewFilter;
  readonly label: string;
}

const FILTER_TABS: readonly FilterTab[] = [
  { id: 'PENDING', label: 'Pending' },
  { id: 'ACCEPTED', label: 'Accepted' },
  { id: 'REJECTED', label: 'Rejected' },
  { id: 'SHORTLIST', label: 'Shortlist' },
  { id: 'ALL', label: 'All' },
];

/**
 * Reviewer dashboard.
 *
 * Shows every nomination in a table with the rule-based flags and Claude's
 * verdict alongside, so the coordinator can see at a glance which ones need a
 * closer look. Both are advisory — accepting and rejecting is entirely the
 * reviewer's call, and a decision can be taken back.
 *
 * Rows are held in a signal and patched in place after each write, because the
 * API returns the updated row. Refetching the whole list after every star would
 * recompute the flags for all 50 nominations to change one boolean.
 */
@Component({
  selector: 'app-review',
  imports: [DatePipe],
  templateUrl: './review.html',
  styleUrl: './review.scss',
})
export class Review {
  private readonly auth = inject(AuthService);
  private readonly reviews = inject(ReviewService);

  protected readonly quarter = CURRENT_QUARTER;
  protected readonly tabs = FILTER_TABS;
  protected readonly firstName = computed(() => this.auth.user()?.name.split(' ')[0] ?? 'there');

  protected readonly rows = signal<NominationView[]>([]);
  protected readonly loading = signal(true);
  protected readonly loadError = signal<string | null>(null);
  protected readonly filter = signal<ReviewFilter>('PENDING');

  /** The one row whose detail panel is open, if any. */
  protected readonly expandedId = signal<number | null>(null);

  /** Rows with a write in flight — their buttons are disabled while it lands. */
  protected readonly busyIds = signal<ReadonlySet<number>>(new Set());

  /** Progress of the "Review all pending" run, or null when it isn't running. */
  protected readonly bulkProgress = signal<{ done: number; total: number } | null>(null);

  protected readonly counts = computed(() => {
    const rows = this.rows();
    return {
      ALL: rows.length,
      PENDING: rows.filter((row) => row.status === 'PENDING').length,
      ACCEPTED: rows.filter((row) => row.status === 'ACCEPTED').length,
      REJECTED: rows.filter((row) => row.status === 'REJECTED').length,
      SHORTLIST: rows.filter((row) => row.favourite).length,
      flagged: rows.filter((row) => row.flags.length > 0).length,
    };
  });

  protected readonly visibleRows = computed(() => {
    const filter = this.filter();
    const rows = this.rows();

    if (filter === 'ALL') {
      return rows;
    }
    if (filter === 'SHORTLIST') {
      return rows.filter((row) => row.favourite);
    }
    return rows.filter((row) => row.status === filter);
  });

  /** Nominations Claude hasn't seen yet — what "Review all pending" works through. */
  protected readonly unreviewed = computed(() =>
    this.rows().filter((row) => row.claudeReview === null),
  );

  constructor() {
    void this.load();
  }

  protected async load(): Promise<void> {
    this.loading.set(true);
    this.loadError.set(null);
    try {
      this.rows.set(await this.reviews.list());
    } catch {
      this.loadError.set(
        'Couldn’t reach the nominations API. Check the Spring Boot app is running on port 8080.',
      );
    } finally {
      this.loading.set(false);
    }
  }

  protected isBusy(id: number): boolean {
    return this.busyIds().has(id);
  }

  protected toggleExpanded(id: number): void {
    this.expandedId.update((current) => (current === id ? null : id));
  }

  protected async toggleFavourite(row: NominationView): Promise<void> {
    await this.write(row.id, () => this.reviews.setFavourite(row.id, !row.favourite));
  }

  protected async decide(row: NominationView, status: ReviewStatus): Promise<void> {
    // Clicking the current decision again takes it back rather than doing nothing.
    const next = row.status === status ? 'PENDING' : status;
    await this.write(row.id, () => this.reviews.setStatus(row.id, next));
  }

  protected async runClaudeReview(row: NominationView): Promise<void> {
    await this.write(row.id, () => this.reviews.requestClaudeReview(row.id));
  }

  /**
   * Reviews every unreviewed nomination, one at a time.
   *
   * Sequential on purpose: each one is a live model call, and firing 50 at once
   * would just collect rate-limit errors. A nomination that fails is skipped
   * rather than stopping the run — the API returns 503 when the model is
   * overloaded, which says nothing about the remaining nominations.
   */
  protected async runAllClaudeReviews(): Promise<void> {
    const queue = this.unreviewed();
    if (queue.length === 0 || this.bulkProgress() !== null) {
      return;
    }

    this.loadError.set(null);
    this.bulkProgress.set({ done: 0, total: queue.length });
    let failed = 0;

    try {
      for (const [index, row] of queue.entries()) {
        if (!(await this.write(row.id, () => this.reviews.requestClaudeReview(row.id)))) {
          failed++;
        }
        this.bulkProgress.set({ done: index + 1, total: queue.length });
      }
    } finally {
      this.bulkProgress.set(null);
    }

    this.loadError.set(
      failed === 0
        ? null
        : `Claude reviewed ${queue.length - failed} of ${queue.length} nominations. ` +
            `${failed} couldn’t be reviewed — the model was busy. Try again for the rest.`,
    );
  }

  /**
   * Runs an API write, marking the row busy and patching the response in.
   * Resolves true when it succeeded.
   */
  private async write(id: number, action: () => Promise<NominationView>): Promise<boolean> {
    if (this.isBusy(id)) {
      return false;
    }

    this.setBusy(id, true);
    try {
      const updated = await action();
      this.rows.update((rows) => rows.map((row) => (row.id === id ? updated : row)));
      return true;
    } catch {
      // During a bulk run the caller replaces this with a run-level summary.
      this.loadError.set(`Couldn’t update nomination #${id}. Please try again.`);
      return false;
    } finally {
      this.setBusy(id, false);
    }
  }

  private setBusy(id: number, busy: boolean): void {
    this.busyIds.update((current) => {
      const next = new Set(current);
      if (busy) {
        next.add(id);
      } else {
        next.delete(id);
      }
      return next;
    });
  }
}
