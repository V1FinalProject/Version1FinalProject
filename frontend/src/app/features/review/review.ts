import { Component, computed, inject } from '@angular/core';
import { CURRENT_QUARTER } from '../../core/models/nomination.model';
import { AuthService } from '../../core/services/auth.service';
import { NominationService } from '../../core/services/nomination.service';

/**
 * Reviewer dashboard — scaffold only.
 *
 * The swipe review UI goes in the placeholder below once the backend can serve
 * nominations and the ML flags. For now it reads whatever the nomination form
 * has written locally, just to prove the data path.
 */
@Component({
  selector: 'app-review',
  imports: [],
  templateUrl: './review.html',
  styleUrl: './review.scss',
})
export class Review {
  private readonly auth = inject(AuthService);
  private readonly nominations = inject(NominationService);

  protected readonly quarter = CURRENT_QUARTER;
  protected readonly firstName = computed(
    () => this.auth.user()?.name.split(' ')[0] ?? 'there',
  );

  /**
   * Read once on load — there's no live source yet, so there is nothing to
   * react to.
   */
  protected readonly pending = this.nominations.readAll();
}
