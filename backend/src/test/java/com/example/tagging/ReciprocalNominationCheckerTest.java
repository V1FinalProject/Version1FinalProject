package com.example.tagging;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.example.tagging.NominationTestData.nomination;
import static org.assertj.core.api.Assertions.assertThat;

class ReciprocalNominationCheckerTest {

    private final ReciprocalNominationChecker checker = new ReciprocalNominationChecker();

    @Test
    void flagsWhenNomineeNominatedTheNominatorBack() {
        Nomination target = nomination().id(1)
                .nominator("Alice", "alice@example.com")
                .nominee("Bob", "bob@example.com")
                .build();
        Nomination reciprocal = nomination().id(2)
                .nominator("Bob", "bob@example.com")
                .nominee("Alice", "alice@example.com")
                .build();

        Optional<FlagResult> result = checker.check(target, List.of(target, reciprocal));

        assertThat(result).isPresent();
        assertThat(result.get().tagName()).isEqualTo("Reciprocal Nomination");
        assertThat(result.get().reasoning()).contains("#2");
    }

    @Test
    void matchIsCaseInsensitiveOnBothEmails() {
        Nomination target = nomination().id(1)
                .nominator("Alice", "alice@example.com")
                .nominee("Bob", "bob@example.com")
                .build();
        Nomination reciprocal = nomination().id(2)
                .nominator("Bob", "BOB@EXAMPLE.COM")
                .nominee("Alice", "ALICE@EXAMPLE.COM")
                .build();

        assertThat(checker.check(target, List.of(target, reciprocal))).isPresent();
    }

    @Test
    void doesNotFlagWhenNoInversePairExists() {
        Nomination target = nomination().id(1)
                .nominator("Alice", "alice@example.com")
                .nominee("Bob", "bob@example.com")
                .build();
        Nomination unrelated = nomination().id(2)
                .nominator("Carol", "carol@example.com")
                .nominee("Dave", "dave@example.com")
                .build();

        assertThat(checker.check(target, List.of(target, unrelated))).isEmpty();
    }

    @Test
    void doesNotTreatTargetAloneAsReciprocal() {
        Nomination target = nomination().id(1)
                .nominator("Alice", "alice@example.com")
                .nominee("Bob", "bob@example.com")
                .build();

        assertThat(checker.check(target, List.of(target))).isEmpty();
    }
}
