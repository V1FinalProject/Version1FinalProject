package com.example.tagging;

import java.time.LocalDateTime;
import java.util.List;

/**
 * One row of the reviewer dashboard: the nomination, everything the tagging
 * checkers found against it, Claude's verdict, and where the reviewer has left
 * it.
 *
 * {@code claudeReview} is null until the nomination has actually been sent to
 * Claude - form submissions are reviewed on the way in, seed rows only when the
 * reviewer asks.
 */
public record NominationView(
        int id,
        LocalDateTime timestamp,
        String nominatorName,
        String nominatorEmail,
        String nomineeName,
        String nomineeEmail,
        String category,
        String what,
        String how,
        String justification,
        String practice,
        String location,
        /** The programme round this nomination belongs to, e.g. {@code "Q4 2026"} - see {@link Quarter}. */
        String quarter,
        List<FlagResult> flags,
        ClaudeReviewResult claudeReview,
        ReviewStatus status,
        boolean favourite,
        /** Null if the nominator's account can't be found (nominations don't require one to exist). */
        PersonSummary nominatorProfile,
        /** Null if the nominee's account can't be found (nominations don't require one to exist). */
        PersonSummary nomineeProfile,
        /** 0-100. See {@link ReciprocityService}. */
        int reciprocityPercent,
        /** How many other times this nominee has been nominated, any quarter, any status. */
        int pastNominationsCount,
        /** Every other nomination this nominator has submitted, most recent first. */
        List<NominationHistoryEntry> nominatorHistory,
        /** Every other nomination this nominee has received, most recent first. */
        List<NominationHistoryEntry> nomineeHistory) {
}
