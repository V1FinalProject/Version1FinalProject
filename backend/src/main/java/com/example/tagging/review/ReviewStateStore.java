package com.example.tagging.review;

import com.example.tagging.claude.ClaudeReviewResult;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The reviewer's own state for each nomination - decision, shortlist star, and
 * the cached Claude verdict - persisted in the {@code review_state} Mongo
 * collection.
 *
 * Claude's verdict is cached because a review costs an API call: once a
 * nomination has been reviewed the answer never changes, since the nomination
 * text is immutable.
 *
 * There is only one reviewer account today, so none of this is keyed by user.
 * When the coordinator role grows past one person, these documents need a
 * user id alongside the nomination id.
 */
@Component
public class ReviewStateStore {

    private final ReviewStateMongoRepository repository;

    public ReviewStateStore(ReviewStateMongoRepository repository) {
        this.repository = repository;
    }

    /** Nominations start life pending - nothing is decided until the reviewer says so. */
    public ReviewStatus statusOf(int nominationId) {
        return repository.findById(nominationId).map(ReviewStateDocument::getStatus).orElse(ReviewStatus.PENDING);
    }

    public void setStatus(int nominationId, ReviewStatus status) {
        ReviewStateDocument document = documentFor(nominationId);
        document.setStatus(status);
        repository.save(document);
    }

    public boolean isFavourite(int nominationId) {
        return repository.findById(nominationId).map(ReviewStateDocument::isFavourite).orElse(false);
    }

    public void setFavourite(int nominationId, boolean favourite) {
        ReviewStateDocument document = documentFor(nominationId);
        document.setFavourite(favourite);
        repository.save(document);
    }

    public Optional<ClaudeReviewResult> claudeReviewOf(int nominationId) {
        return repository.findById(nominationId).map(ReviewStateDocument::getClaudeReview);
    }

    public void saveClaudeReview(int nominationId, ClaudeReviewResult result) {
        ReviewStateDocument document = documentFor(nominationId);
        document.setClaudeReview(result);
        repository.save(document);
    }

    private ReviewStateDocument documentFor(int nominationId) {
        return repository.findById(nominationId).orElseGet(() -> new ReviewStateDocument(nominationId));
    }
}
