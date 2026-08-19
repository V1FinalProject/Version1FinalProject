package com.example.tagging;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

// Flags nominations whose WHAT/HOW text is short, vague, and lacks both a
// quantifiable impact and a link to a stated company value. This uses a plain
// three-signal score rather than sentiment analysis - tweak the constants and
// STATED_VALUES below to adjust it. Swap this implementation for an Azure OpenAI
// based checker later without touching NominationFlagChecker or its callers.
@Component
public class WeakJustificationChecker implements NominationFlagChecker {

    private static final int SHORT_TEXT_THRESHOLD_CHARS = 150;
    private static final int FLAG_SCORE_THRESHOLD = 2;
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");

    @Override
    public Optional<FlagResult> check(Nomination target, List<Nomination> allNominations) {
        String text = target.justification();
        String lowerText = text.toLowerCase();

        List<String> reasons = new ArrayList<>();
        int score = 0;

        if (text.length() < SHORT_TEXT_THRESHOLD_CHARS) {
            score++;
            reasons.add("justification is short (" + text.length() + " characters)");
        }

        if (!DIGIT_PATTERN.matcher(text).find()) {
            score++;
            reasons.add("no quantifiable impact found (no numbers/figures)");
        }

        boolean mentionsStatedValue = CompanyValues.STATED_VALUES.stream().anyMatch(lowerText::contains);
        if (!mentionsStatedValue) {
            score++;
            reasons.add("does not mention a stated company value");
        }

        if (score < FLAG_SCORE_THRESHOLD) {
            return Optional.empty();
        }

        String reasoning = "Weak justification (score " + score + "/3): " + String.join("; ", reasons);
        return Optional.of(new FlagResult("Weak Justification", reasoning));
    }
}
