package com.example.tagging.auditlog;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The reviewer dashboard's audit trail - one entry per action taken on a
 * nomination, from creation through review decisions, so there's a record of
 * who changed what and when.
 */
@Service
public class AuditLogService {

    private final AuditLogMongoRepository repository;

    public AuditLogService(AuditLogMongoRepository repository) {
        this.repository = repository;
    }

    public void record(int nominationId, String nomineeName, String nominatorName, String action) {
        repository.insert(AuditLogEntry.of(nominationId, nomineeName, nominatorName, action));
    }

    /** Every entry, newest first. */
    public List<AuditLogEntry> findAll() {
        return repository.findAllByOrderByTimestampDesc();
    }
}
