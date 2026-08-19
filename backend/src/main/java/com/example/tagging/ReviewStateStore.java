package com.example.tagging;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The reviewer's own state for each nomination - decision, shortlist star, and
 * the cached Claude verdict - held in memory alongside NominationStore.
 *
 * Claude's verdict is cached because a review costs an API call: once a
 * nomination has been reviewed the answer never changes, since the nomination
 * text is immutable.
 *
 * There is only one reviewer account today, so none of this is keyed by user.
 * When the coordinator role grows past one person, these maps need a user id in
 * the key (or, by then, a row in the database).
 */
@Component
public class ReviewStateStore {

    private final Map<Integer, ReviewStatus> statuses = new ConcurrentHashMap<>();
    private final Set<Integer> favourites = ConcurrentHashMap.newKeySet();
    private final Map<Integer, ClaudeReviewResult> claudeReviews = new ConcurrentHashMap<>();

    /** Nominations start life pending - nothing is decided until the reviewer says so. */
    public ReviewStatus statusOf(int nominationId) {
        return statuses.getOrDefault(nominationId, ReviewStatus.PENDING);
    }

    public void setStatus(int nominationId, ReviewStatus status) {
        statuses.put(nominationId, status);
    }

    public boolean isFavourite(int nominationId) {
        return favourites.contains(nominationId);
    }

    public void setFavourite(int nominationId, boolean favourite) {
        if (favourite) {
            favourites.add(nominationId);
        } else {
            favourites.remove(nominationId);
        }
    }

    public Optional<ClaudeReviewResult> claudeReviewOf(int nominationId) {
        return Optional.ofNullable(claudeReviews.get(nominationId));
    }

    public void saveClaudeReview(int nominationId, ClaudeReviewResult result) {
        claudeReviews.put(nominationId, result);
    }
}
