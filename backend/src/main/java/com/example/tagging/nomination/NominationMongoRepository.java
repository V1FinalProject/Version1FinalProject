package com.example.tagging.nomination;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/** Mongo-backed access to the {@code nominations} collection. */
public interface NominationMongoRepository extends MongoRepository<Nomination, Integer> {

    /** Used to pick up id assignment where the collection left off, on startup. */
    Optional<Nomination> findFirstByOrderByIdDesc();

    /** Backs the one-nomination-per-quarter rule in {@link NominationStore#add}. */
    boolean existsByNominatorEmailIgnoreCaseAndQuarter(String nominatorEmail, String quarter);
}
