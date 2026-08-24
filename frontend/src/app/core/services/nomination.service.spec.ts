import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { beforeEach, afterEach, describe, expect, it } from 'vitest';

import { NominationService } from './nomination.service';
import { NominationSubmission } from '../models/nomination.model';

function sampleSubmission(): NominationSubmission {
  return {
    nomineeName: 'Bob',
    nomineeEmail: 'bob@version1.com',
    what: 'shipped the release',
    how: 'with drive',
    categoryId: 'customer-impact',
    emailReceipt: false,
    nominatorId: 'u-001',
    nominatorName: 'Alice',
    nominatorEmail: 'alice@version1.com',
    practice: 'Engineering',
    location: 'Dublin',
    quarter: 'Q4 2026',
    submittedAt: '2026-05-01T09:00:00.000Z',
  };
}

describe('NominationService (API path)', () => {
  let service: NominationService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [NominationService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(NominationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('POSTs the submission to /api/nominations and returns the receipt', async () => {
    const submission = sampleSubmission();
    const receipt = { reference: 'SA-2026-0042', submittedAt: submission.submittedAt };

    const promise = service.submit(submission);

    const req = httpMock.expectOne('/api/nominations');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(submission);
    req.flush(receipt);

    await expect(promise).resolves.toEqual(receipt);
  });

  it('does not touch localStorage on the API path', async () => {
    const promise = service.submit(sampleSubmission());
    httpMock.expectOne('/api/nominations').flush({ reference: 'SA-2026-0001', submittedAt: 'x' });
    await promise;

    expect(localStorage.getItem('star-awards.nominations')).toBeNull();
  });

  it('rejects when the submission POST fails', async () => {
    const promise = service.submit(sampleSubmission());

    httpMock
      .expectOne('/api/nominations')
      .flush('bad request', { status: 400, statusText: 'Bad Request' });

    await expect(promise).rejects.toMatchObject({ status: 400 });
  });
});
