package com.example.tagging;

/**
 * Claude's verdict on a nomination: whether it's a genuine nomination at all,
 * and whether it reflects Version1's stated values.
 */
public record ClaudeReviewResult(boolean isValidNomination, boolean isVersion1Values) {
}
