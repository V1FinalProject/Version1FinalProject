import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuditLogEntry } from '../../core/models/audit-log.model';
import { AuditLogService } from '../../core/services/audit-log.service';

/**
 * The audit trail: nomination submissions and every reviewer action
 * (accept/reject/pending, favourite, mark voucher sent), newest first — see
 * AuditLogController on the backend.
 */
@Component({
  selector: 'app-logs',
  imports: [DatePipe, RouterLink],
  templateUrl: './logs.html',
  styleUrl: './logs.scss',
})
export class Logs {
  private readonly auditLog = inject(AuditLogService);

  protected readonly rows = signal<AuditLogEntry[]>([]);
  protected readonly loading = signal(true);
  protected readonly loadError = signal<string | null>(null);

  constructor() {
    void this.load();
  }

  protected async load(): Promise<void> {
    this.loading.set(true);
    this.loadError.set(null);
    try {
      this.rows.set(await this.auditLog.list());
    } catch {
      this.loadError.set(
        'Couldn’t reach the audit log API. Check the Spring Boot app is running on port 8080.',
      );
    } finally {
      this.loading.set(false);
    }
  }
}
