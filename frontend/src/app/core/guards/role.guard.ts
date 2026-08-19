import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Restricts the review dashboard to coordinators (currently
 * `reviewer@version1.com`).
 *
 * Nominating is open to everyone; reviewing is not. Signed-out visitors go to
 * login, signed-in employees are bounced to the form rather than shown an
 * error — they have no reason to be here.
 */
export const reviewerGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const user = auth.user();

  if (!user) {
    return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
  }

  return user.role === 'coordinator' ? true : router.createUrlTree(['/nominate']);
};

/**
 * The mirror of `reviewerGuard`: coordinators can't submit nominations.
 *
 * Two reasons. `reviewer@version1.com` is a shared role account rather than a
 * person, so it has no nominee of its own; and a coordinator who nominates
 * would end up reviewing their own submission, which is exactly the kind of
 * conflict the human-in-the-loop review is meant to protect.
 *
 * Runs after `authGuard`, so the user is already known to be signed in.
 */
export const nominatorGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.user()?.role === 'coordinator' ? router.createUrlTree(['/review']) : true;
};
