package com.example.tagging;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.tagging.NominationTestData.nomination;
import static org.assertj.core.api.Assertions.assertThat;

class NotEmployeeStatusCheckerTest {

    private final NotEmployeeStatusChecker checker = new NotEmployeeStatusChecker();

    @Test
    void neverFlagsWhileItIsAPlaceholder() {
        Nomination target = nomination().build();
        assertThat(checker.check(target, List.of(target))).isEmpty();
    }

    @Test
    void neverFlagsEvenWithNoOtherNominations() {
        assertThat(checker.check(nomination().build(), List.of())).isEmpty();
    }
}
