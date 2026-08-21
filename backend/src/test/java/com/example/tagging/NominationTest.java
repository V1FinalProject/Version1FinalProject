package com.example.tagging;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NominationTest {

    private static final LocalDateTime WHEN = LocalDateTime.of(2025, 5, 1, 9, 0);

    @Test
    void fromPartsBuildsCombinedJustificationAndCategoryLabel() {
        Nomination nomination = Nomination.fromParts(7, WHEN, "Alice", "alice@example.com",
                "Bob", "bob@example.com", "shipped the release", "with real drive",
                NominationCategory.PERFORMANCE_AND_EFFICIENCY, "Engineering", "London");

        assertThat(nomination.id()).isEqualTo(7);
        assertThat(nomination.what()).isEqualTo("shipped the release");
        assertThat(nomination.how()).isEqualTo("with real drive");
        assertThat(nomination.category()).isEqualTo("Performance & Efficiency");
        assertThat(nomination.justification())
                .isEqualTo("WHAT: shipped the release\n\nHOW: with real drive");
        assertThat(nomination.practice()).isEqualTo("Engineering");
        assertThat(nomination.location()).isEqualTo("London");
    }

    @Test
    void fromJustificationSplitsOnWhatAndHowMarkers() {
        String blob = "WHAT: delivered the migration\n\nHOW: showed excellence";

        Nomination nomination = Nomination.fromJustification(3, WHEN, "Alice", "alice@example.com",
                "Bob", "bob@example.com", blob, "Customer Impact");

        assertThat(nomination.what()).isEqualTo("delivered the migration");
        assertThat(nomination.how()).isEqualTo("showed excellence");
        assertThat(nomination.justification()).isEqualTo(blob);
        // Nominator practice/location predate the seed rows and stay null.
        assertThat(nomination.practice()).isNull();
        assertThat(nomination.location()).isNull();
    }

    @Test
    void fromJustificationWithoutHowMarkerPutsEverythingInWhat() {
        String blob = "WHAT: just a single blob of text with no how section";

        Nomination nomination = Nomination.fromJustification(4, WHEN, "Alice", "alice@example.com",
                "Bob", "bob@example.com", blob, "Customer Impact");

        assertThat(nomination.what()).isEqualTo("just a single blob of text with no how section");
        assertThat(nomination.how()).isEmpty();
    }

    @Test
    void fromJustificationWithoutAnyMarkersKeepsRawTextAsWhat() {
        String blob = "he was consistently excellent all quarter";

        Nomination nomination = Nomination.fromJustification(5, WHEN, "Alice", "alice@example.com",
                "Bob", "bob@example.com", blob, "Customer Impact");

        assertThat(nomination.what()).isEqualTo(blob);
        assertThat(nomination.how()).isEmpty();
        assertThat(nomination.justification()).isEqualTo(blob);
    }
}
