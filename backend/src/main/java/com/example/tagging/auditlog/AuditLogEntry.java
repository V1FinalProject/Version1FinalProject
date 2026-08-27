package com.example.tagging.auditlog;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * One entry in the reviewer dashboard's audit trail: an action taken on a
 * nomination, from creation through to a reviewer's decision.
 *
 * Append-only - entries are never updated once written, so unlike
 * {@code ReviewStateDocument} there's no upsert-by-nomination-id pattern here;
 * Mongo assigns its own id on insert.
 */
@Document(collection = "audit_log")
public record AuditLogEntry(
        @Id String id,
        int nominationId,
        String nomineeName,
        String nominatorName,
        LocalDateTime timestamp,
        String action) {

    public static AuditLogEntry of(int nominationId, String nomineeName, String nominatorName, String action) {
        return new AuditLogEntry(null, nominationId, nomineeName, nominatorName, LocalDateTime.now(), action);
    }
}
