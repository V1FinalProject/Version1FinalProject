package com.example.tagging;

import java.time.LocalDateTime;

/**
 * Small builder for {@link Nomination} instances in tests, so each test only has
 * to set the fields it actually cares about instead of the full 12-arg record.
 */
final class NominationTestData {

    private int id = 1;
    private LocalDateTime timestamp = LocalDateTime.of(2025, 5, 1, 9, 0);
    private String nominatorName = "Alice Nominator";
    private String nominatorEmail = "alice@example.com";
    private String nomineeName = "Bob Nominee";
    private String nomineeEmail = "bob@example.com";
    private String justification = "A perfectly detailed justification with plenty of substance.";
    private String category = "Customer Impact";
    private String what = "did a thing";
    private String how = "with excellence";
    private String practice = "Engineering";
    private String location = "London";

    static NominationTestData nomination() {
        return new NominationTestData();
    }

    NominationTestData id(int id) {
        this.id = id;
        return this;
    }

    NominationTestData timestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    NominationTestData nominator(String name, String email) {
        this.nominatorName = name;
        this.nominatorEmail = email;
        return this;
    }

    NominationTestData nominee(String name, String email) {
        this.nomineeName = name;
        this.nomineeEmail = email;
        return this;
    }

    NominationTestData justification(String justification) {
        this.justification = justification;
        return this;
    }

    NominationTestData category(String category) {
        this.category = category;
        return this;
    }

    Nomination build() {
        return new Nomination(id, timestamp, nominatorName, nominatorEmail, nomineeName, nomineeEmail,
                justification, category, what, how, practice, location);
    }
}
