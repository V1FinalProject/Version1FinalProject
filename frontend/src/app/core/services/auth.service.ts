import { Injectable, computed, signal } from '@angular/core';
import { AppUser, DEMO_USERS } from '../models/user.model';

const SESSION_KEY = 'star-awards.session';

/**
 * Stand-in authentication.
 *
 * There is no real identity provider yet, so this matches an email against
 * `DEMO_USERS`, accepts any non-empty password, and keeps the session in
 * localStorage so a refresh doesn't sign you out.
 *
 * When real auth arrives, only `signIn` and `restore` need replacing — the rest
 * of the app just reads `user()` and `isAuthenticated()`.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly _user = signal<AppUser | null>(restoreSession());

  readonly user = this._user.asReadonly();
  readonly isAuthenticated = computed(() => this._user() !== null);

  /**
   * Resolves to an error message on failure, or null on success.
   * Async because the real endpoint will be.
   */
  async signIn(email: string, password: string): Promise<string | null> {
    // Simulate network latency so the loading state is visible.
    await new Promise((resolve) => setTimeout(resolve, 400));

    if (!password.trim()) {
      return 'Please enter your password.';
    }

    const match = DEMO_USERS.find(
      (candidate) => candidate.email.toLowerCase() === email.trim().toLowerCase(),
    );

    if (!match) {
      return 'We don’t recognise that email address. Try one of the demo accounts below.';
    }

    this._user.set(match);
    localStorage.setItem(SESSION_KEY, JSON.stringify(match));
    return null;
  }

  signOut(): void {
    this._user.set(null);
    localStorage.removeItem(SESSION_KEY);
  }
}

function restoreSession(): AppUser | null {
  try {
    const stored = localStorage.getItem(SESSION_KEY);
    return stored ? (JSON.parse(stored) as AppUser) : null;
  } catch {
    // Corrupt or unreadable storage — start signed out rather than crashing.
    return null;
  }
}
