import { DatePipe } from '@angular/common';
import { Component, computed, HostListener, inject, signal } from '@angular/core';
import { CURRENT_QUARTER, NOMINATION_CATEGORIES } from '../../core/models/nomination.model';
import { NominationView, ReviewStatus } from '../../core/models/review.model';
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
 * Colour variant per flag, keyed by the backend's tag name (see the
 * NominationFlagChecker implementations in com.example.tagging). A tag with no
 * entry here — e.g. one from a checker added later — just gets the neutral
 * default `.chip--flag` colour rather than breaking.
 */
const FLAG_CLASSES: Readonly<Record<string, string>> = {
  'Weak Justification': 'chip--flag-weak',
  'Routine Task Language': 'chip--flag-routine',
  'Reciprocal Nomination': 'chip--flag-reciprocal',
  'Repeat Nomination': 'chip--flag-repeat',
};

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
  private readonly reviews = inject(ReviewService);

  protected readonly quarter = CURRENT_QUARTER;
  protected readonly tabs = FILTER_TABS;
  /** The five fixed nomination categories, for the filter dropdown. */
  protected readonly categoryOptions = NOMINATION_CATEGORIES;

  protected readonly rows = signal<NominationView[]>([]);
  protected readonly loading = signal(true);
  protected readonly loadError = signal<string | null>(null);
  protected readonly filter = signal<ReviewFilter>('PENDING');

  /** Free-text search, matched against nominee and nominator name. */
  protected readonly searchQuery = signal('');

  /** Categories currently checked in the dropdown. Empty set = no category filter. */
  protected readonly selectedCategories = signal<ReadonlySet<string>>(new Set());
  protected readonly categoryMenuOpen = signal(false);

  /** The one row whose detail panel is open, if any. */
  protected readonly expandedId = signal<number | null>(null);

  /**
   * Nested sub-dropdowns inside the open detail panel (org chart, nomination
   * history). Only one row is ever expanded at a time, so these don't need to
   * be keyed by row id — they're reset whenever the open row changes.
   */
  protected readonly orgChartOpen = signal(false);
  protected readonly historyOpen = signal(false);

  /**
   * Nominations marked "voucher sent" in this session. There's no backend
   * field for this yet, so it's UI-only bookkeeping — it resets on reload
   * and isn't shared between reviewers.
   */
  protected readonly voucherSentIds = signal<ReadonlySet<number>>(new Set());

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

  /**
   * The status tab, category checkboxes and name search all narrow the same
   * list together (AND, not OR) — e.g. the "Pending" tab plus a category pick
   * shows only pending nominations in that category.
   */
  protected readonly visibleRows = computed(() => {
    const filter = this.filter();
    const categories = this.selectedCategories();
    const query = this.searchQuery().trim().toLowerCase();
    let rows = this.rows();

    if (filter === 'SHORTLIST') {
      rows = rows.filter((row) => row.favourite);
    } else if (filter !== 'ALL') {
      rows = rows.filter((row) => row.status === filter);
    }

    if (categories.size > 0) {
      rows = rows.filter((row) => categories.has(row.category));
    }

    if (query) {
      rows = rows.filter(
        (row) =>
          row.nomineeName.toLowerCase().includes(query) ||
          row.nominatorName.toLowerCase().includes(query),
      );
    }

    return rows;
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

  protected isCategorySelected(label: string): boolean {
    return this.selectedCategories().has(label);
  }

  protected toggleCategory(label: string): void {
    this.selectedCategories.update((current) => {
      const next = new Set(current);
      if (next.has(label)) {
        next.delete(label);
      } else {
        next.add(label);
      }
      return next;
    });
  }

  protected clearCategories(): void {
    this.selectedCategories.set(new Set());
  }

  protected toggleCategoryMenu(): void {
    this.categoryMenuOpen.update((open) => !open);
  }

  /** Closes the category dropdown when clicking anywhere outside it. */
  @HostListener('document:click', ['$event'])
  protected onDocumentClick(event: MouseEvent): void {
    if (!this.categoryMenuOpen()) {
      return;
    }
    if (!(event.target as HTMLElement).closest('.category-filter')) {
      this.categoryMenuOpen.set(false);
    }
  }

  /** Colour class for a flag chip, so each flag type reads as its own colour. */
  protected flagClass(tagName: string): string {
    return FLAG_CLASSES[tagName] ?? '';
  }

  protected toggleExpanded(id: number): void {
    this.expandedId.update((current) => (current === id ? null : id));
    // Sub-dropdowns belong to whichever row is open — don't leak "open" state
    // from one nominee's org chart into the next row you expand.
    this.orgChartOpen.set(false);
    this.historyOpen.set(false);
  }

  protected toggleOrgChart(): void {
    this.orgChartOpen.update((open) => !open);
  }

  protected toggleHistory(): void {
    this.historyOpen.update((open) => !open);
  }

  protected isVoucherSent(id: number): boolean {
    return this.voucherSentIds().has(id);
  }

  protected toggleVoucherSent(id: number): void {
    this.voucherSentIds.update((current) => {
      const next = new Set(current);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
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
