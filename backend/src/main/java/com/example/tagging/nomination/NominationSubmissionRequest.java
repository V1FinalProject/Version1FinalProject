package com.example.tagging.nomination;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * The body of POST /api/nominations, matching the NominationSubmission
 * interface the frontend sends.
 *
 * The nominator block is captured from the signed-in user rather than typed, so
 * it arrives with the submission instead of being looked up here.
 */
public record NominationSubmissionRequest(
        String nomineeName,
        String nomineeEmail,
        String what,
        String how,
        String categoryId,
        /** Accepted for forward compatibility - there is no mail service yet. */
        boolean emailReceipt,

        String nominatorId,
        String nominatorName,
        String nominatorEmail,
        String practice,
        String location,

        String quarter,
        Instant submittedAt) {

    /**
     * Checks every required field and resolves the category, throwing a 400 on
     * anything missing or unrecognised.
     *
     * Split out from {@link #toNomination(int)} so the store can reject a bad
     * submission before it claims an id - the id becomes the nominator's
     * reference number, so a gap in the sequence looks like a lost nomination.
     */
    NominationCategory validate() {
        require(nomineeName, "nomineeName");
        require(nomineeEmail, "nomineeEmail");
        require(what, "what");
        require(how, "how");
        require(nominatorName, "nominatorName");
        require(nominatorEmail, "nominatorEmail");

        // The frontend already blocks this in the form, but the API is the real
        // boundary - anyone calling it directly should hit the same rule.
        if (nominatorEmail.trim().equalsIgnoreCase(nomineeEmail.trim())) {
            throw badRequest("You cannot nominate yourself");
        }

        return NominationCategory.byId(require(categoryId, "categoryId"))
                .orElseThrow(() -> badRequest("Unknown categoryId \"" + categoryId + "\""));
    }

    Nomination toNomination(int id) {
        NominationCategory category = validate();
        Instant submitted = submittedAt == null ? Instant.now() : submittedAt;

        return Nomination.fromParts(
                id,
                LocalDateTime.ofInstant(submitted, ZoneOffset.UTC),
                require(nominatorName, "nominatorName"),
                require(nominatorEmail, "nominatorEmail").toLowerCase(),
                require(nomineeName, "nomineeName"),
                require(nomineeEmail, "nomineeEmail").toLowerCase(),
                require(what, "what"),
                require(how, "how"),
                category,
                practice,
                location);
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw badRequest(field + " is required");
        }
        return value.trim();
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
