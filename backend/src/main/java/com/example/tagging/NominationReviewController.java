package com.example.tagging;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class NominationReviewController {

    private final NominationExcelLoader loader;
    private final ClaudeNominationReviewer reviewer;

    public NominationReviewController(NominationExcelLoader loader, ClaudeNominationReviewer reviewer) {
        this.loader = loader;
        this.reviewer = reviewer;
    }

    @GetMapping("/api/nominations/{id}/claude-review")
    public ClaudeReviewResult review(@PathVariable int id) {
        List<Nomination> nominations = loader.loadAll();
        Nomination target = nominations.stream()
                .filter(nomination -> nomination.id() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No nomination found with id " + id));

        return reviewer.review(target);
    }
}
