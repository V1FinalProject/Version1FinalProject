package com.example.tagging.nomination;

import java.time.ZoneOffset;

/**
 * What the nominator gets back after submitting - a human-readable reference
 * they can quote, and the time we recorded.
 */
public record NominationReceipt(String reference, String submittedAt) {

    public static NominationReceipt forNomination(Nomination nomination) {
        String reference = "SA-%d-%04d".formatted(nomination.timestamp().getYear(), nomination.id());
        return new NominationReceipt(reference, nomination.timestamp().toInstant(ZoneOffset.UTC).toString());
    }
}
