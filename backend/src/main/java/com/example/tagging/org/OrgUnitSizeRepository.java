package com.example.tagging.org;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/** Mongo-backed access to the {@code org_units} collection. */
public interface OrgUnitSizeRepository extends MongoRepository<OrgUnitSize, String> {

    Optional<OrgUnitSize> findByNameAndType(String name, OrgUnitType type);
}
