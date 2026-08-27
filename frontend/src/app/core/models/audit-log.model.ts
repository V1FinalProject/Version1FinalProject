/** One row of the /logs audit trail — mirrors AuditLogEntry in com.example.tagging.auditlog. */
export interface AuditLogEntry {
  id: string;
  nominationId: number;
  nomineeName: string;
  nominatorName: string;
  /** ISO 8601, no zone — the backend stores UTC. */
  timestamp: string;
  action: string;
}
