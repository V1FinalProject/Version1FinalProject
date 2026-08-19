package com.example.tagging;

/** Body of PUT /api/nominations/{id}/decision. */
public record ReviewDecisionRequest(ReviewStatus status) {
}
