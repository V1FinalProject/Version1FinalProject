package com.example.tagging;

import org.springframework.data.mongodb.repository.MongoRepository;

/** Mongo-backed access to the {@code review_state} collection. */
public interface ReviewStateMongoRepository extends MongoRepository<ReviewStateDocument, Integer> {
}
