package com.example.tagging;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.tagging.NominationTestData.nomination;
import static org.assertj.core.api.Assertions.assertThat;

class RepeatNominationCheckerTest {

    private final RepeatNominationChecker checker = new RepeatNominationChecker();

    @Test
    void flagsSameNomineeNominatedInImmediatelyPrecedingQuarter() {
        // Prior in Q1 (Feb), target in Q2 (May) -> preceding quarter matches.
        Nomination prior = nomination().id(1)
                .timestamp(LocalDateTime.of(2025, 2, 10, 9, 0))
                .nominee("Bob Nominee", "bob@example.com")
                .build();
        Nomination target = nomination().id(2)
                .timestamp(LocalDateTime.of(2025, 5, 10, 9, 0))
                .nominee("Bob Nominee", "bob@example.com")
                .build();

        Optional<FlagResult> result = checker.check(target, List.of(prior, target));

        assertThat(result).isPresent();
        assertThat(result.get().tagName()).isEqualTo("Repeat Nomination");
        assertThat(result.get().reasoning()).contains("Q1 2025").contains("#1");
    }

    @Test
    void crossesYearBoundaryFromQ1BackToPreviousQ4() {
        Nomination prior = nomination().id(1)
                .timestamp(LocalDateTime.of(2024, 11, 1, 9, 0)) // Q4 2024
                .nominee("Bob Nominee", "bob@example.com")
                .build();
        Nomination target = nomination().id(2)
                .timestamp(LocalDateTime.of(2025, 1, 15, 9, 0)) // Q1 2025
                .nominee("Bob Nominee", "bob@example.com")
                .build();

        Optional<FlagResult> result = checker.check(target, List.of(prior, target));

        assertThat(result).isPresent();
        assertThat(result.get().reasoning()).contains("Q4 2024");
    }

    @Test
    void doesNotFlagWhenPriorIsSameQuarterNotPreceding() {
        Nomination prior = nomination().id(1)
                .timestamp(LocalDateTime.of(2025, 5, 1, 9, 0)) // same Q2
                .nominee("Bob Nominee", "bob@example.com")
                .build();
        Nomination target = nomination().id(2)
                .timestamp(LocalDateTime.of(2025, 6, 1, 9, 0)) // Q2
                .nominee("Bob Nominee", "bob@example.com")
                .build();

        assertThat(checker.check(target, List.of(prior, target))).isEmpty();
    }

    @Test
    void doesNotFlagDifferentNominee() {
        Nomination prior = nomination().id(1)
                .timestamp(LocalDateTime.of(2025, 2, 10, 9, 0))
                .nominee("Carol Other", "carol@example.com")
                .build();
        Nomination target = nomination().id(2)
                .timestamp(LocalDateTime.of(2025, 5, 10, 9, 0))
                .nominee("Bob Nominee", "bob@example.com")
                .build();

        assertThat(checker.check(target, List.of(prior, target))).isEmpty();
    }

    @Test
    void nomineeEmailMatchIsCaseInsensitive() {
        Nomination prior = nomination().id(1)
                .timestamp(LocalDateTime.of(2025, 2, 10, 9, 0))
                .nominee("Bob Nominee", "BOB@EXAMPLE.COM")
                .build();
        Nomination target = nomination().id(2)
                .timestamp(LocalDateTime.of(2025, 5, 10, 9, 0))
                .nominee("Bob Nominee", "bob@example.com")
                .build();

        assertThat(checker.check(target, List.of(prior, target))).isPresent();
    }

    @Test
    void ignoresTheTargetItselfWhenScanning() {
        Nomination target = nomination().id(2)
                .timestamp(LocalDateTime.of(2025, 5, 10, 9, 0))
                .nominee("Bob Nominee", "bob@example.com")
                .build();

        assertThat(checker.check(target, List.of(target))).isEmpty();
    }
}
