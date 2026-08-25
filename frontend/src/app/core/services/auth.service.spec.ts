import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { AuthService } from './auth.service';
import { AppUser } from '../models/user.model';

const SESSION_KEY = 'star-awards.session';
const LOGIN_URL = '/api/auth/login';

const sampleUser: AppUser = {
  id: 'u-1',
  name: 'Alice Byrne',
  email: 'alice.byrne@version1.com',
  practice: 'Engineering',
  location: 'Dublin',
  role: 'employee',
};

describe('AuthService', () => {
  let httpMock: HttpTestingController;

  /**
   * Constructs AuthService fresh, after whatever localStorage state a test
   * has set up — construction is what reads the stored session, so a
   * service built too early (e.g. in a shared beforeEach) would miss it.
   */
  function createService(): AuthService {
    return TestBed.inject(AuthService);
  }

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [AuthService, provideHttpClient(), provideHttpClientTesting()],
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('starts signed out with a clean session', () => {
    const service = createService();
    expect(service.isAuthenticated()).toBe(false);
    expect(service.user()).toBeNull();
  });

  it('signs in against the real login endpoint and persists the session', async () => {
    const service = createService();
    const promise = service.signIn(sampleUser.email, 'correct-password');

    const req = httpMock.expectOne(LOGIN_URL);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email: sampleUser.email, password: 'correct-password' });
    req.flush(sampleUser);

    const error = await promise;

    expect(error).toBeNull();
    expect(service.isAuthenticated()).toBe(true);
    expect(service.user()).toEqual(sampleUser);
    expect(JSON.parse(localStorage.getItem(SESSION_KEY)!)).toEqual(sampleUser);
  });

  it('trims whitespace from the email before sending it', async () => {
    const service = createService();
    const promise = service.signIn(`  ${sampleUser.email}  `, 'pw');

    const req = httpMock.expectOne(LOGIN_URL);
    expect(req.request.body.email).toBe(sampleUser.email);
    req.flush(sampleUser);

    await promise;
  });

  it('reports a 401 as incorrect email or password, without setting a user', async () => {
    const service = createService();
    const promise = service.signIn(sampleUser.email, 'wrong-password');

    httpMock.expectOne(LOGIN_URL).flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    const error = await promise;

    expect(error).toBe('Incorrect email or password.');
    expect(service.isAuthenticated()).toBe(false);
    expect(localStorage.getItem(SESSION_KEY)).toBeNull();
  });

  it('falls back to a connection error message on any other failure', async () => {
    const service = createService();
    const promise = service.signIn(sampleUser.email, 'pw');

    httpMock.expectOne(LOGIN_URL).flush('boom', { status: 500, statusText: 'Server Error' });

    const error = await promise;

    expect(error).toContain('Couldn’t reach the sign-in service');
    expect(service.isAuthenticated()).toBe(false);
  });

  it('signs out and clears the stored session', async () => {
    const service = createService();
    const promise = service.signIn(sampleUser.email, 'pw');
    httpMock.expectOne(LOGIN_URL).flush(sampleUser);
    await promise;

    service.signOut();

    expect(service.isAuthenticated()).toBe(false);
    expect(service.user()).toBeNull();
    expect(localStorage.getItem(SESSION_KEY)).toBeNull();
  });

  it('restores an existing session from localStorage on construction', () => {
    localStorage.setItem(SESSION_KEY, JSON.stringify(sampleUser));

    const restored = createService();

    expect(restored.isAuthenticated()).toBe(true);
    expect(restored.user()).toEqual(sampleUser);
  });

  it('starts signed out when the stored session is corrupt', () => {
    localStorage.setItem(SESSION_KEY, '{not valid json');

    const restored = createService();

    expect(restored.isAuthenticated()).toBe(false);
    expect(restored.user()).toBeNull();
  });
});
