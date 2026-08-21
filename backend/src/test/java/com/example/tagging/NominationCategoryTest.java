package com.example.tagging;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class NominationCategoryTest {

    @Test
    void resolvesKnownIdToItsCategory() {
        assertThat(NominationCategory.byId("customer-impact"))
                .contains(NominationCategory.CUSTOMER_IMPACT);
    }

    @Test
    void idLookupIsCaseInsensitive() {
        assertThat(NominationCategory.byId("Innovation-And-Growth"))
                .contains(NominationCategory.INNOVATION_AND_GROWTH);
    }

    @Test
    void returnsEmptyForUnknownId() {
        assertThat(NominationCategory.byId("no-such-category")).isEmpty();
    }

    @Test
    void exposesIdAndLabel() {
        NominationCategory category = NominationCategory.QUALITY_AND_COMPLIANCE;
        assertThat(category.id()).isEqualTo("quality-and-compliance");
        assertThat(category.label()).isEqualTo("Quality & Compliance");
    }

    @Test
    void everyCategoryIdRoundTripsBackToItself() {
        for (NominationCategory category : NominationCategory.values()) {
            assertThat(NominationCategory.byId(category.id())).contains(category);
        }
    }
}
