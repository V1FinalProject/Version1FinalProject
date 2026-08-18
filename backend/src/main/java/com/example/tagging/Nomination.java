package com.example.tagging;

import java.time.LocalDateTime;

/**
 * A single Star Awards nomination, matching the columns of the mock nominations spreadsheet.
 */
public record Nomination(
        int id,
        LocalDateTime timestamp,
        String nominatorName,
        String nominatorEmail,
        String nomineeName,
        String nomineeEmail,
        String justification,
        String category) {
}
