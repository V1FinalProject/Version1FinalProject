import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NominationReceipt, NominationSubmission } from '../models/nomination.model';

/** Where the `useMockApi` fallback keeps submissions when the API is off. */
const STORAGE_KEY = 'star-awards.nominations';

@Injectable({ providedIn: 'root' })
export class NominationService {
  private readonly http = inject(HttpClient);

  /**
   * Submits a nomination to `POST {apiBaseUrl}/nominations`.
   *
   * The backend stores it and asks Claude to review it before responding, so
   * this can take a couple of seconds — the form shows a spinner meanwhile.
   */
  async submit(submission: NominationSubmission): Promise<NominationReceipt> {
    if (environment.useMockApi) {
      return this.submitToLocalStorage(submission);
    }

    return firstValueFrom(
      this.http.post<NominationReceipt>(`${environment.apiBaseUrl}/nominations`, submission),
    );
  }

  /** Everything submitted so far in this browser. Only used by the mock path. */
  private readAll(): NominationSubmission[] {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      return stored ? (JSON.parse(stored) as NominationSubmission[]) : [];
    } catch {
      return [];
    }
  }

  private async submitToLocalStorage(
    submission: NominationSubmission,
  ): Promise<NominationReceipt> {
    await new Promise((resolve) => setTimeout(resolve, 600));

    const existing = this.readAll();
    existing.push(submission);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(existing));

    const year = new Date(submission.submittedAt).getUTCFullYear();
    const sequence = String(existing.length).padStart(4, '0');

    return { reference: `SA-${year}-${sequence}`, submittedAt: submission.submittedAt };
  }
}
