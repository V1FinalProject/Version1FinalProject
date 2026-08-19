import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NominationReceipt, NominationSubmission } from '../models/nomination.model';

const STORAGE_KEY = 'star-awards.nominations';

@Injectable({ providedIn: 'root' })
export class NominationService {
  private readonly http = inject(HttpClient);

  /**
   * Submits a nomination.
   *
   * Once the Spring Boot endpoint exists, set `useMockApi: false` in the
   * environment file and this goes over HTTP unchanged.
   */
  async submit(submission: NominationSubmission): Promise<NominationReceipt> {
    if (environment.useMockApi) {
      return this.submitToLocalStorage(submission);
    }

    return firstValueFrom(
      this.http.post<NominationReceipt>(`${environment.apiBaseUrl}/nominations`, submission),
    );
  }

  /** Everything submitted so far in this browser — useful while there is no backend. */
  readAll(): NominationSubmission[] {
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
