package com.example.tagging.nomination;

import java.util.Arrays;
import java.util.Optional;

/**
 * The five nomination categories, mirroring NOMINATION_CATEGORIES in the
 * frontend's nomination.model.ts.
 *
 * The form submits the id; the spreadsheet seed data carries the label. Both
 * are resolved here so a nomination always ends up holding the same display
 * label whichever way it arrived.
 */
public enum NominationCategory {

    COLLABORATION_AND_ENGAGEMENT("collaboration-and-engagement", "Collaboration & Engagement"),
    CUSTOMER_IMPACT("customer-impact", "Customer Impact"),
    INNOVATION_AND_GROWTH("innovation-and-growth", "Innovation & Growth"),
    PERFORMANCE_AND_EFFICIENCY("performance-and-efficiency", "Performance & Efficiency"),
    QUALITY_AND_COMPLIANCE("quality-and-compliance", "Quality & Compliance");

    private final String id;
    private final String label;

    NominationCategory(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String id() {
        return id;
    }

    public String label() {
        return label;
    }

    public static Optional<NominationCategory> byId(String id) {
        return Arrays.stream(values())
                .filter(category -> category.id.equalsIgnoreCase(id))
                .findFirst();
    }
}
