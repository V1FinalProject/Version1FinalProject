import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { NominationView, ReviewStatus } from '../models/review.model';

/**
 * The reviewer dashboard's half of the nomination API.
 *
 * Submitting a nomination lives in `NominationService` — this is everything the
 * coordinator does with the results afterwards. Every write returns the updated
 * row so the caller can patch it in place rather than refetching the list.
 */
@Injectable({ providedIn: 'root' })
export class ReviewService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/nominations`;

  /** Every nomination, newest first, with flags and review state attached. */
  list(): Promise<NominationView[]> {
    return firstValueFrom(this.http.get<NominationView[]>(this.baseUrl));
  }

  /** Accept, reject, or move a nomination back to pending. */
  setStatus(id: number, status: ReviewStatus): Promise<NominationView> {
    return firstValueFrom(
      this.http.put<NominationView>(`${this.baseUrl}/${id}/decision`, { status }),
    );
  }

  /** Star or unstar a nomination for the shortlist. */
  setFavourite(id: number, favourite: boolean): Promise<NominationView> {
    return firstValueFrom(
      this.http.put<NominationView>(`${this.baseUrl}/${id}/favourite`, { favourite }),
    );
  }

  /**
   * Sends a nomination to Claude and returns the updated row.
   *
   * Slow — it's a live model call — but the backend caches the verdict, so
   * asking twice for the same nomination is free the second time.
   */
  requestClaudeReview(id: number): Promise<NominationView> {
    return firstValueFrom(this.http.post<NominationView>(`${this.baseUrl}/${id}/claude-review`, {}));
  }

  /**
   * Records "Mark voucher sent" being ticked or unticked in the audit log.
   * There's no persisted voucher-sent field yet, so this is a log-only write.
   */
  logVoucherSent(id: number, sent: boolean): Promise<void> {
    return firstValueFrom(this.http.put<void>(`${this.baseUrl}/${id}/voucher-sent`, { sent }));
  }
}
