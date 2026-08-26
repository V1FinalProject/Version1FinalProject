package com.example.tagging.claude;

import com.example.tagging.nomination.CompanyValues;
import com.example.tagging.nomination.Nomination;
import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import org.springframework.stereotype.Service;

// Sends a nomination to Claude to flag whether it looks like a genuine
// nomination and whether it reflects Version1's stated values.
@Service
public class ClaudeNominationReviewer {

    private static final String SYSTEM_PROMPT = """
            You are reviewing a nomination submitted to Version1's internal Star Awards \
            peer-recognition program. You will be given the nominee, category, and the \
            nominator's written justification.

            Decide two things:
            - isValidNomination: false if the justification is spam, gibberish, a joke, \
              obviously fake, or otherwise not a genuine attempt to nominate a colleague. \
              true otherwise.
            - isVersion1Values: true if the justification's content demonstrates one or \
              more of Version1's stated values (%s). false if it doesn't connect to any \
              of them, even if it's a genuine nomination.
            """.formatted(String.join(", ", CompanyValues.STATED_VALUES));

    private final AnthropicClient client;

    public ClaudeNominationReviewer(AnthropicClient client) {
        this.client = client;
    }

    public ClaudeReviewResult review(Nomination nomination) {
        String userPrompt = """
                Nominee: %s
                Category: %s
                Justification: %s
                """.formatted(nomination.nomineeName(), nomination.category(), nomination.justification());

        StructuredMessageCreateParams<ClaudeReviewResult> params = MessageCreateParams.builder()
                .model("claude-haiku-4-5-20251001")
                .maxTokens(4096L)
                .system(SYSTEM_PROMPT)
                .outputConfig(ClaudeReviewResult.class)
                .addUserMessage(userPrompt)
                .build();

        return client.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .map(typed -> typed.text())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Claude did not return a structured review for nomination " + nomination.id()));
    }
}
