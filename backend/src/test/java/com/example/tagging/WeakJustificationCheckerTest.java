package com.example.tagging;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.example.tagging.NominationTestData.nomination;
import static org.assertj.core.api.Assertions.assertThat;

class WeakJustificationCheckerTest {

    private final WeakJustificationChecker checker = new WeakJustificationChecker();

    @Test
    void flagsShortJustificationWithNoNumbersAndNoValue() {
        // Score 3/3: short, no digits, no stated value.
        Nomination target = nomination().justification("He is nice.").build();

        Optional<FlagResult> result = checker.check(target, List.of(target));

        assertThat(result).isPresent();
        assertThat(result.get().tagName()).isEqualTo("Weak Justification");
        assertThat(result.get().reasoning()).contains("score 3/3");
    }

    @Test
    void doesNotFlagWhenScoreBelowThreshold() {
        // Long text that mentions a value and a number scores 0 -> no flag.
        String strong = "Bob showed real excellence when he cut processing time by 40 percent "
                + "over the quarter, coordinating three teams and rewriting the batch pipeline "
                + "to remove a long-standing bottleneck that had frustrated customers for months.";
        Nomination target = nomination().justification(strong).build();

        assertThat(checker.check(target, List.of(target))).isEmpty();
    }

    @Test
    void flagsAtExactlyTwoSignals() {
        // Long text (>=150 chars) with a stated value but no digits -> score 1, no flag...
        String longNoDigits = "Bob demonstrated genuine excellence throughout the whole project, "
                + "supporting everyone around him and lifting the standard of work across the team "
                + "in a way that colleagues noticed and appreciated every single day of the release.";
        assertThat(checker.check(nomination().justification(longNoDigits).build(),
                List.of())).isEmpty();

        // Short text with no digits (2 signals) but mentions a value -> still 2 -> flagged.
        Nomination twoSignals = nomination().justification("Showed great excellence.").build();
        Optional<FlagResult> result = checker.check(twoSignals, List.of(twoSignals));
        assertThat(result).isPresent();
        assertThat(result.get().reasoning()).contains("score 2/3");
    }

    @Test
    void digitAnywhereSatisfiesQuantifiableSignal() {
        // Short + mentions value would be 2, but a digit drops it to 1 -> no flag.
        Nomination target = nomination().justification("Saved 5 hours; real drive.").build();
        assertThat(checker.check(target, List.of(target))).isEmpty();
    }

    @Test
    void statedValueMatchIsCaseInsensitive() {
        // "HONESTY & INTEGRITY" in upper case must still count as a stated value.
        Nomination target = nomination()
                .justification("Bob showed HONESTY & INTEGRITY handling the 12 escalations.")
                .build();
        assertThat(checker.check(target, List.of(target))).isEmpty();
    }
}
