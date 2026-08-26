import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { beforeEach, afterEach, describe, expect, it } from 'vitest';

import { ReviewService } from './review.service';
import { NominationView } from '../models/review.model';

const BASE = '/api/nominations';

function sampleRow(overrides: Partial<NominationView> = {}): NominationView {
  return {
    id: 1,
    timestamp: '2026-05-01T09:00:00',
    nominatorName: 'Alice',
    nominatorEmail: 'alice@version1.com',
    nomineeName: 'Bob',
    nomineeEmail: 'bob@version1.com',
    category: 'Customer Impact',
    what: 'did a thing',
    how: 'with excellence',
    justification: 'WHAT: did a thing\n\nHOW: with excellence',
    practice: 'Engineering',
    location: 'Dublin',
    quarter: 'Q4 2026',
    flags: [],
    claudeReview: null,
    status: 'PENDING',
    favourite: false,
    nominatorProfile: null,
    nomineeProfile: null,
    reciprocityPercent: 0,
    pastNominationsCount: 0,
    nominatorHistory: [],
    nomineeHistory: [],
    ...overrides,
  };
}

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ReviewService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('GETs the full nomination list', async () => {
    const rows = [sampleRow({ id: 1 }), sampleRow({ id: 2 })];
    const promise = service.list();

    const req = httpMock.expectOne(BASE);
    expect(req.request.method).toBe('GET');
    req.flush(rows);

    await expect(promise).resolves.toEqual(rows);
  });

  it('PUTs a status decision to the decision endpoint', async () => {
    const updated = sampleRow({ status: 'ACCEPTED' });
    const promise = service.setStatus(7, 'ACCEPTED');

    const req = httpMock.expectOne(`${BASE}/7/decision`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ status: 'ACCEPTED' });
    req.flush(updated);

    await expect(promise).resolves.toEqual(updated);
  });

  it('PUTs a favourite toggle to the favourite endpoint', async () => {
    const updated = sampleRow({ favourite: true });
    const promise = service.setFavourite(3, true);

    const req = httpMock.expectOne(`${BASE}/3/favourite`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ favourite: true });
    req.flush(updated);

    await expect(promise).resolves.toEqual(updated);
  });

  it('POSTs a Claude review request with an empty body', async () => {
    const updated = sampleRow({ claudeReview: { isValidNomination: true, isVersion1Values: true } });
    const promise = service.requestClaudeReview(5);

    const req = httpMock.expectOne(`${BASE}/5/claude-review`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    req.flush(updated);

    await expect(promise).resolves.toEqual(updated);
  });

  it('rejects when the API errors', async () => {
    const promise = service.list();

    httpMock.expectOne(BASE).flush('boom', { status: 500, statusText: 'Server Error' });

    await expect(promise).rejects.toMatchObject({ status: 500 });
  });
});
