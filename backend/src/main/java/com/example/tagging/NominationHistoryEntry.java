package com.example.tagging;

/**
 * One earlier nomination shown in the reviewer detail panel's "previous
 * nominations" history for a single person (either the current nomination's
 * nominator or its nominee). Unlike the old nominator-only/nominee-only split,
 * this covers both directions that person was involved in - {@code direction}
 * says whether they were the one nominating ({@code counterpartName} is who
 * they nominated) or the one nominated ({@code counterpartName} is who
 * nominated them).
 *
 * {@code reciprocal} is true when this pair have nominated each other at some
 * point - any quarter, any status, including the nomination currently being
 * reviewed - which is what earns the person's name the bold-and-red treatment
 * in the panel.
 */
public record NominationHistoryEntry(int id, String quarter, String category, String counterpartName,
        ReviewStatus status, HistoryDirection direction, boolean reciprocal) {

    public enum HistoryDirection {
        OUTBOUND,
        INBOUND
    }
}
