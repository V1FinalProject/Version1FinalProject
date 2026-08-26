package com.example.tagging.nomination;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * A single Star Awards nomination.
 *
 * The mock spreadsheet had one free-text "why are you nominating this
 * colleague?" column; the web form splits the same question into WHAT (the
 * achievement and its impact) and HOW (the values it demonstrated). Both shapes
 * are represented here: {@code what}/{@code how} hold the split, and
 * {@code justification} always holds the full text, so the flag checkers read
 * one field and never have to care which shape a nomination arrived in.
 *
 * {@code practice} and {@code location} come from the signed-in nominator and
 * are null on the spreadsheet seed rows, which predate those fields.
 *
 * {@code quarter} is the programme round label (e.g. {@code "Q4 2026"}) the
 * frontend sends as {@code CURRENT_QUARTER} - see {@link Quarter}. It is not
 * derived from {@code timestamp}: the programme's quarters don't follow the
 * calendar, so the two can't be inferred from one another.
 *
 * Persisted directly as a Mongo document - {@code id} doubles as the
 * nominator's reference number, so it stays a plain incrementing {@code int}
 * (assigned by {@link NominationStore}) rather than an ObjectId.
 */
@Document(collection = "nominations")
public record Nomination(
        @Id int id,
        LocalDateTime timestamp,
        String nominatorName,
        String nominatorEmail,
        String nomineeName,
        String nomineeEmail,
        String justification,
        String category,
        String what,
        String how,
        String practice,
        String location,
        String quarter) {

    private static final String WHAT_MARKER = "WHAT:";
    private static final String HOW_MARKER = "HOW:";

    /** Builds a nomination from the WHAT/HOW split the web form collects. */
    public static Nomination fromParts(int id, LocalDateTime timestamp, String nominatorName,
            String nominatorEmail, String nomineeName, String nomineeEmail, String what, String how,
            NominationCategory category, String practice, String location, String quarter) {

        String justification = WHAT_MARKER + " " + what + "\n\n" + HOW_MARKER + " " + how;

        return new Nomination(id, timestamp, nominatorName, nominatorEmail, nomineeName, nomineeEmail,
                justification, category.label(), what, how, practice, location, quarter);
    }

    /**
     * Builds a nomination from a single justification blob, splitting it on the
     * WHAT:/HOW: markers the seed data uses. Text without those markers becomes
     * the WHAT half, leaving HOW empty rather than guessing where to cut.
     */
    public static Nomination fromJustification(int id, LocalDateTime timestamp, String nominatorName,
            String nominatorEmail, String nomineeName, String nomineeEmail, String justification,
            String category, String quarter) {

        int howStart = justification.indexOf(HOW_MARKER);
        String whatPart = howStart < 0 ? justification : justification.substring(0, howStart);
        String howPart = howStart < 0 ? "" : justification.substring(howStart + HOW_MARKER.length());

        return new Nomination(id, timestamp, nominatorName, nominatorEmail, nomineeName, nomineeEmail,
                justification, category, stripMarker(whatPart, WHAT_MARKER), howPart.trim(), null, null, quarter);
    }

    private static String stripMarker(String text, String marker) {
        String trimmed = text.trim();
        return trimmed.startsWith(marker) ? trimmed.substring(marker.length()).trim() : trimmed;
    }
}
