package com.example.tagging;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.example.tagging.NominationTestData.nomination;
import static org.assertj.core.api.Assertions.assertThat;

class TaggingServiceTest {

    private final Nomination target = nomination().build();

    @Test
    void collectsFlagsFromEveryCheckerThatFires() {
        NominationFlagChecker flags = (t, all) -> Optional.of(new FlagResult("A", "reason a"));
        NominationFlagChecker passes = (t, all) -> Optional.empty();
        NominationFlagChecker alsoFlags = (t, all) -> Optional.of(new FlagResult("B", "reason b"));

        TaggingService service = new TaggingService(List.of(flags, passes, alsoFlags));

        List<FlagResult> results = service.evaluate(target, List.of(target));

        assertThat(results).extracting(FlagResult::tagName).containsExactly("A", "B");
    }

    @Test
    void returnsEmptyWhenNoCheckerFires() {
        TaggingService service = new TaggingService(List.of(
                (t, all) -> Optional.empty(),
                (t, all) -> Optional.empty()));

        assertThat(service.evaluate(target, List.of(target))).isEmpty();
    }

    @Test
    void returnsEmptyWhenThereAreNoCheckers() {
        TaggingService service = new TaggingService(List.of());
        assertThat(service.evaluate(target, List.of(target))).isEmpty();
    }

    @Test
    void passesBothArgumentsThroughToCheckers() {
        List<Nomination> all = List.of(target, nomination().id(2).build());
        NominationFlagChecker recording = (t, seen) -> {
            assertThat(t).isSameAs(target);
            assertThat(seen).isSameAs(all);
            return Optional.empty();
        };

        new TaggingService(List.of(recording)).evaluate(target, all);
    }
}
