package com.example.tagging;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Flags nominations whose justification reads as generic/routine language rather
// than a specific achievement. This is a plain keyword scan, not sentiment analysis -
// tweak ROUTINE_PHRASES to adjust it. Swap this implementation for an Azure OpenAI
// based checker later without touching NominationFlagChecker or its callers.
@Component
public class RoutineLanguageChecker implements NominationFlagChecker {

    private static final List<String> ROUTINE_PHRASES = List.of(
            "did what was asked",
            "completed the task",
            "attended all meetings",
            "followed the process",
            "team player",
            "did not deviate",
            "up to date with",
            "consistent attendance",
            "present and available",
            "good attitude",
            "team spirit",
            "supportive colleague",
            "is reliable",
            "as usual");

    @Override
    public Optional<FlagResult> check(Nomination target, List<Nomination> allNominations) {
        String text = target.justification().toLowerCase();

        List<String> matchedPhrases = ROUTINE_PHRASES.stream()
                .filter(text::contains)
                .collect(Collectors.toList());

        if (matchedPhrases.isEmpty()) {
            return Optional.empty();
        }

        String reasoning = "Justification uses routine/generic language: matched \""
                + String.join("\", \"", matchedPhrases) + "\"";

        return Optional.of(new FlagResult("Routine Task Language", reasoning));
    }
}
