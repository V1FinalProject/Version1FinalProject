package com.example.tagging;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Runs a nomination through every registered NominationFlagChecker and collects
// the flags that apply.
@Service
public class TaggingService {

    private final List<NominationFlagChecker> checkers;

    public TaggingService(List<NominationFlagChecker> checkers) {
        this.checkers = checkers;
    }

    public List<FlagResult> evaluate(Nomination target, List<Nomination> allNominations) {
        return checkers.stream()
                .map(checker -> checker.check(target, allNominations))
                .flatMap(Optional::stream)
                .toList();
    }
}
