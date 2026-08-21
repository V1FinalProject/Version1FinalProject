package com.example.tagging;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NominationSubmissionRequestTest {

    /** Local builder so each test tweaks one field of an otherwise-valid request. */
    private static final class NominationSubmissionRequestBuilder {
        String nomineeName = "Bob";
        String nomineeEmail = "bob@example.com";
        String what = "shipped the release";
        String how = "with drive";
        String categoryId = "customer-impact";
        String nominatorName = "Alice";
        String nominatorEmail = "alice@example.com";
        Instant submittedAt = Instant.parse("2025-05-01T09:00:00Z");

        NominationSubmissionRequest build() {
            return new NominationSubmissionRequest(nomineeName, nomineeEmail, what, how, categoryId,
                    false, "nom-1", nominatorName, nominatorEmail, "Engineering", "London",
                    "Q2 2025", submittedAt);
        }
    }

    @Test
    void validateReturnsResolvedCategoryForAGoodRequest() {
        NominationCategory category = new NominationSubmissionRequestBuilder().build().validate();
        assertThat(category).isEqualTo(NominationCategory.CUSTOMER_IMPACT);
    }

    @Test
    void rejectsMissingNomineeName() {
        NominationSubmissionRequestBuilder builder = new NominationSubmissionRequestBuilder();
        builder.nomineeName = "  ";

        assertThatThrownBy(() -> builder.build().validate())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("nomineeName is required");
    }

    @Test
    void rejectsSelfNomination() {
        NominationSubmissionRequestBuilder builder = new NominationSubmissionRequestBuilder();
        builder.nomineeEmail = "Alice@Example.com"; // same as nominator, different case/space

        assertThatThrownBy(() -> builder.build().validate())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("cannot nominate yourself");
    }

    @Test
    void rejectsUnknownCategory() {
        NominationSubmissionRequestBuilder builder = new NominationSubmissionRequestBuilder();
        builder.categoryId = "made-up";

        assertThatThrownBy(() -> builder.build().validate())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Unknown categoryId");
    }

    @Test
    void badRequestsCarryA400Status() {
        NominationSubmissionRequestBuilder builder = new NominationSubmissionRequestBuilder();
        builder.what = null;

        assertThatThrownBy(() -> builder.build().validate())
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void toNominationLowercasesEmailsAndTrimsFields() {
        NominationSubmissionRequestBuilder builder = new NominationSubmissionRequestBuilder();
        builder.nomineeEmail = "  BOB@EXAMPLE.COM ";
        builder.nominatorEmail = "  ALICE@EXAMPLE.COM ";

        Nomination nomination = builder.build().toNomination(42);

        assertThat(nomination.id()).isEqualTo(42);
        assertThat(nomination.nomineeEmail()).isEqualTo("bob@example.com");
        assertThat(nomination.nominatorEmail()).isEqualTo("alice@example.com");
        assertThat(nomination.category()).isEqualTo("Customer Impact");
    }

    @Test
    void toNominationDefaultsTimestampToNowWhenNotSupplied() {
        NominationSubmissionRequestBuilder builder = new NominationSubmissionRequestBuilder();
        builder.submittedAt = null;

        Nomination nomination = builder.build().toNomination(1);

        assertThat(nomination.timestamp()).isNotNull();
    }
}
