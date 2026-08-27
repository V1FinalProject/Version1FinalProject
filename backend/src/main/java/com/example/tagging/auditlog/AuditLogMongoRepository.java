package com.example.tagging.auditlog;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/** Mongo-backed access to the {@code audit_log} collection. */
public interface AuditLogMongoRepository extends MongoRepository<AuditLogEntry, String> {

    List<AuditLogEntry> findAllByOrderByTimestampDesc();
}
