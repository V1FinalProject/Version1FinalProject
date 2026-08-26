package com.example.tagging.review;

/** Body of PUT /api/nominations/{id}/decision. */
public record ReviewDecisionRequest(ReviewStatus status) {
}
