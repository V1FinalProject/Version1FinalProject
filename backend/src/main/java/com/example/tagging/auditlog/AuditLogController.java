package com.example.tagging.auditlog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Read-only view of the reviewer dashboard's audit trail, for the {@code /logs} page. */
@RestController
public class AuditLogController {

    private final AuditLogService auditLog;

    public AuditLogController(AuditLogService auditLog) {
        this.auditLog = auditLog;
    }

    @GetMapping("/api/audit-log")
    public List<AuditLogEntry> list() {
        return auditLog.findAll();
    }
}
