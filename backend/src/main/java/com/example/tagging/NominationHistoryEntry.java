package com.example.tagging;

/**
 * One earlier nomination shown in the reviewer detail panel's "previous
 * nominations" history - either something the nominator submitted before, or
 * something the nominee received before. Which side it's showing determines
 * what {@code counterpartName} means: the nominee they nominated, or the
 * nominator who nominated them.
 */
public record NominationHistoryEntry(int id, String quarter, String category, String counterpartName,
        ReviewStatus status) {
}
