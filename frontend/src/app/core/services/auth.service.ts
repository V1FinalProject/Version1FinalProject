import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AppUser } from '../models/user.model';

const SESSION_KEY = 'star-awards.session';

/**
 * Authentication against the real `users` collection via
 * `POST /api/auth/login`.
 *
 * No session/token beyond that: a successful login's response is kept in a
 * signal and mirrored to localStorage so a refresh doesn't sign you out. Real
 * identity (Microsoft SSO) would change `signIn`/`restore`; nothing else in
 * the app would need to change, since the rest just reads
 * `user()`/`isAuthenticated()`.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly _user = signal<AppUser | null>(restoreSession());

  readonly user = this._user.asReadonly();
  readonly isAuthenticated = computed(() => this._user() !== null);

  /** Resolves to an error message on failure, or null on success. */
  async signIn(email: string, password: string): Promise<string | null> {
    try {
      const user = await firstValueFrom(
        this.http.post<AppUser>(`${environment.apiBaseUrl}/auth/login`, {
          email: email.trim(),
          password,
        }),
      );
      this._user.set(user);
      localStorage.setItem(SESSION_KEY, JSON.stringify(user));
      return null;
    } catch (error) {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        return 'Incorrect email or password.';
      }
      return 'Couldn’t reach the sign-in service. Please check your connection and try again.';
    }
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
