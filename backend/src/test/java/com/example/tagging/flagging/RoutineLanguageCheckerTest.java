package com.example.tagging.flagging;

import com.example.tagging.nomination.Nomination;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.example.tagging.flagging.NominationTestData.nomination;
import static org.assertj.core.api.Assertions.assertThat;

class RoutineLanguageCheckerTest {

    private final RoutineLanguageChecker checker = new RoutineLanguageChecker();

    @Test
    void flagsWhenARoutinePhraseIsPresent() {
        Nomination target = nomination()
                .justification("Bob completed the task and was a great team player all year.")
                .build();

        Optional<FlagResult> result = checker.check(target, List.of(target));

        assertThat(result).isPresent();
        assertThat(result.get().tagName()).isEqualTo("Routine Task Language");
        assertThat(result.get().reasoning())
                .contains("completed the task")
                .contains("team player");
    }

    @Test
    void matchingIsCaseInsensitive() {
        Nomination target = nomination().justification("He DID WHAT WAS ASKED, nothing more.").build();
        assertThat(checker.check(target, List.of(target))).isPresent();
    }

    @Test
    void doesNotFlagSpecificAchievementLanguage() {
        Nomination target = nomination()
                .justification("Bob redesigned the checkout flow and lifted conversion by 12 percent.")
                .build();
        assertThat(checker.check(target, List.of(target))).isEmpty();
    }
}
