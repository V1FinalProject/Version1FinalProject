package com.example.tagging;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Claude's review of a single nomination.
 *
 * Sending a nomination to Claude costs an API call, so the result is cached
 * after the first review - both verbs return the same cached verdict once one
 * exists.
 */
@RestController
public class NominationReviewController {

    private final NominationReviewService reviewService;

    public NominationReviewController(NominationReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/api/nominations/{id}/claude-review")
    public ClaudeReviewResult review(@PathVariable int id) {
        return reviewService.claudeReview(id);
    }

    /**
     * Same thing, for the dashboard's "Review" button - the reviewer is asking
     * for work to be done, not reading a resource.
     */
    @PostMapping("/api/nominations/{id}/claude-review")
    public NominationView requestReview(@PathVariable int id) {
        reviewService.claudeReview(id);
        return reviewService.find(id);
    }
}
