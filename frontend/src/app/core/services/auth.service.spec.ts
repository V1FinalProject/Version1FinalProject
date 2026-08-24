import { TestBed } from '@angular/core/testing';
import { beforeEach, afterEach, describe, expect, it, vi } from 'vitest';

import { AuthService } from './auth.service';
import { DEMO_USERS } from '../models/user.model';

const SESSION_KEY = 'star-awards.session';

describe('AuthService', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  function createService(): AuthService {
    return TestBed.configureTestingModule({}).inject(AuthService);
  }

  /** signIn awaits a simulated 400ms latency, so drive the fake clock forward. */
  async function signIn(service: AuthService, email: string, password: string) {
    const pending = service.signIn(email, password);
    await vi.advanceTimersByTimeAsync(400);
    return pending;
  }

  it('starts signed out with a clean session', () => {
    const service = createService();
    expect(service.isAuthenticated()).toBe(false);
    expect(service.user()).toBeNull();
  });

  it('signs in a known demo user and persists the session', async () => {
    const service = createService();
    const employee = DEMO_USERS.find((u) => u.role === 'employee')!;

    const error = await signIn(service, employee.email, 'anything');

    expect(error).toBeNull();
    expect(service.isAuthenticated()).toBe(true);
    expect(service.user()).toEqual(employee);
    expect(JSON.parse(localStorage.getItem(SESSION_KEY)!)).toEqual(employee);
  });

  it('matches the email case-insensitively and trims whitespace', async () => {
    const service = createService();
    const user = DEMO_USERS[0];

    const error = await signIn(service, `  ${user.email.toUpperCase()}  `, 'pw');

    expect(error).toBeNull();
    expect(service.user()).toEqual(user);
  });

  it('rejects an empty password without setting a user', async () => {
    const service = createService();

    const error = await signIn(service, DEMO_USERS[0].email, '   ');

    expect(error).toBe('Please enter your password.');
    expect(service.isAuthenticated()).toBe(false);
    expect(localStorage.getItem(SESSION_KEY)).toBeNull();
  });

  it('rejects an unknown email address', async () => {
    const service = createService();

    const error = await signIn(service, 'nobody@version1.com', 'pw');

    expect(error).toContain('don’t recognise');
    expect(service.isAuthenticated()).toBe(false);
  });

  it('signs out and clears the stored session', async () => {
    const service = createService();
    await signIn(service, DEMO_USERS[0].email, 'pw');

    service.signOut();

    expect(service.isAuthenticated()).toBe(false);
    expect(service.user()).toBeNull();
    expect(localStorage.getItem(SESSION_KEY)).toBeNull();
  });

  it('restores an existing session from localStorage on construction', () => {
    const coordinator = DEMO_USERS.find((u) => u.role === 'coordinator')!;
    localStorage.setItem(SESSION_KEY, JSON.stringify(coordinator));

    const service = createService();

    expect(service.isAuthenticated()).toBe(true);
    expect(service.user()).toEqual(coordinator);
  });

  it('starts signed out when the stored session is corrupt', () => {
    localStorage.setItem(SESSION_KEY, '{not valid json');

    const service = createService();

    expect(service.isAuthenticated()).toBe(false);
    expect(service.user()).toBeNull();
  });
});
