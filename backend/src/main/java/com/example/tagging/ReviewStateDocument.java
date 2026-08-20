package com.example.tagging;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The reviewer's own state for one nomination - decision, shortlist star, and
 * the cached Claude verdict - persisted alongside {@link Nomination} in Mongo.
 *
 * One document per nomination, upserted by {@link ReviewStateStore} on first
 * write (a nomination has no review state until the reviewer touches it).
 *
 * There is only one reviewer account today, so none of this is keyed by user.
 * When the coordinator role grows past one person, this needs a user id in the
 * key too.
 */
@Document(collection = "review_state")
public class ReviewStateDocument {

    @Id
    private int nominationId;
    private ReviewStatus status = ReviewStatus.PENDING;
    private boolean favourite;
    private ClaudeReviewResult claudeReview;

    public ReviewStateDocument() {
    }

    public ReviewStateDocument(int nominationId) {
        this.nominationId = nominationId;
    }

    public int getNominationId() {
        return nominationId;
    }

    public void setNominationId(int nominationId) {
        this.nominationId = nominationId;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public ClaudeReviewResult getClaudeReview() {
        return claudeReview;
    }

    public void setClaudeReview(ClaudeReviewResult claudeReview) {
        this.claudeReview = claudeReview;
    }
}
