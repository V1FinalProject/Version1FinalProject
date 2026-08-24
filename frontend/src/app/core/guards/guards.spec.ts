import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { signal } from '@angular/core';
import { beforeEach, describe, expect, it } from 'vitest';

import { authGuard } from './auth.guard';
import { reviewerGuard, nominatorGuard } from './role.guard';
import { AuthService } from '../services/auth.service';
import { AppUser } from '../models/user.model';

const EMPLOYEE: AppUser = {
  id: 'u-001',
  name: 'Joe Duffy',
  email: 'joe.duffy@version1.com',
  practice: 'Digital, Data & Cloud',
  location: 'Dublin',
  role: 'employee',
};

const COORDINATOR: AppUser = { ...EMPLOYEE, id: 'u-005', role: 'coordinator' };

/** Minimal AuthService double exposing the two members the guards read. */
function fakeAuth(user: AppUser | null) {
  return {
    user: signal(user).asReadonly(),
    isAuthenticated: signal(user !== null).asReadonly(),
  };
}

/** Runs a CanActivateFn inside an injection context with a stubbed auth user. */
function runGuard(guard: typeof authGuard, user: AppUser | null, url = '/target') {
  TestBed.configureTestingModule({
    providers: [{ provide: AuthService, useValue: fakeAuth(user) }],
  });
  const router = TestBed.inject(Router);
  return TestBed.runInInjectionContext(() =>
    guard({} as never, { url } as never, ),
  ) as boolean | UrlTree;
}

function pathOf(tree: UrlTree): string {
  return tree.root.children['primary']?.segments.map((s) => s.path).join('/') ?? '';
}

describe('authGuard', () => {
  beforeEach(() => localStorage.clear());

  it('allows a signed-in user through', () => {
    expect(runGuard(authGuard, EMPLOYEE)).toBe(true);
  });

  it('redirects a signed-out visitor to /login with the returnUrl preserved', () => {
    const result = runGuard(authGuard, null, '/review') as UrlTree;
    expect(result).toBeInstanceOf(UrlTree);
    expect(pathOf(result)).toBe('login');
    expect(result.queryParams['returnUrl']).toBe('/review');
  });
});

describe('reviewerGuard', () => {
  beforeEach(() => localStorage.clear());

  it('allows a coordinator into the review dashboard', () => {
    expect(runGuard(reviewerGuard, COORDINATOR)).toBe(true);
  });

  it('bounces a signed-in employee to /nominate', () => {
    const result = runGuard(reviewerGuard, EMPLOYEE) as UrlTree;
    expect(pathOf(result)).toBe('nominate');
  });

  it('sends a signed-out visitor to /login with the returnUrl', () => {
    const result = runGuard(reviewerGuard, null, '/review') as UrlTree;
    expect(pathOf(result)).toBe('login');
    expect(result.queryParams['returnUrl']).toBe('/review');
  });
});

describe('nominatorGuard', () => {
  beforeEach(() => localStorage.clear());

  it('allows an employee to submit nominations', () => {
    expect(runGuard(nominatorGuard, EMPLOYEE)).toBe(true);
  });

  it('redirects a coordinator to /review', () => {
    const result = runGuard(nominatorGuard, COORDINATOR) as UrlTree;
    expect(pathOf(result)).toBe('review');
  });
});
