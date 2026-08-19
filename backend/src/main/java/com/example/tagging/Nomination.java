package com.example.tagging;

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
 */
public record Nomination(
        int id,
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
        String location) {

    private static final String WHAT_MARKER = "WHAT:";
    private static final String HOW_MARKER = "HOW:";

    /** Builds a nomination from the WHAT/HOW split the web form collects. */
    public static Nomination fromParts(int id, LocalDateTime timestamp, String nominatorName,
            String nominatorEmail, String nomineeName, String nomineeEmail, String what, String how,
            NominationCategory category, String practice, String location) {

        String justification = WHAT_MARKER + " " + what + "\n\n" + HOW_MARKER + " " + how;

        return new Nomination(id, timestamp, nominatorName, nominatorEmail, nomineeName, nomineeEmail,
                justification, category.label(), what, how, practice, location);
    }

    /**
     * Builds a nomination from a single justification blob, splitting it on the
     * WHAT:/HOW: markers the seed data uses. Text without those markers becomes
     * the WHAT half, leaving HOW empty rather than guessing where to cut.
     */
    public static Nomination fromJustification(int id, LocalDateTime timestamp, String nominatorName,
            String nominatorEmail, String nomineeName, String nomineeEmail, String justification,
            String category) {

        int howStart = justification.indexOf(HOW_MARKER);
        String whatPart = howStart < 0 ? justification : justification.substring(0, howStart);
        String howPart = howStart < 0 ? "" : justification.substring(howStart + HOW_MARKER.length());

        return new Nomination(id, timestamp, nominatorName, nominatorEmail, nomineeName, nomineeEmail,
                justification, category, stripMarker(whatPart, WHAT_MARKER), howPart.trim(), null, null);
    }

    private static String stripMarker(String text, String marker) {
        String trimmed = text.trim();
        return trimmed.startsWith(marker) ? trimmed.substring(marker.length()).trim() : trimmed;
    }
}
