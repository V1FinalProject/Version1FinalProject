import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuditLogEntry } from '../models/audit-log.model';

/** The /logs page's view of the API: the reviewer dashboard's audit trail. */
@Injectable({ providedIn: 'root' })
export class AuditLogService {
  private readonly http = inject(HttpClient);

  /** Every audit log entry, newest first. */
  list(): Promise<AuditLogEntry[]> {
    return firstValueFrom(this.http.get<AuditLogEntry[]>(`${environment.apiBaseUrl}/audit-log`));
  }
}
