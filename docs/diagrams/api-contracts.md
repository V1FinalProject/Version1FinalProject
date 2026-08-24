# API Contract Layer

*Part of the [Star Awards class diagrams](README.md).*

The request and response shapes that cross HTTP between the Angular app and the Spring Boot API. `NominationView` is the important one — it's a full row on the reviewer dashboard.

```mermaid
classDiagram
    class NominationView {
        <<record>>
        int id
        String category
        List~FlagResult~ flags
        ClaudeReviewResult claudeReview
        ReviewStatus status
        boolean favourite
    }

    class PersonSummary {
        <<record>>
        String jobTitle
        String department
        String contractType
    }

    class NominatableUser {
        <<record>>
        String name
        String email
        String department
    }

    class AuthenticatedUser {
        <<record>>
        String name
        String email
        String role
    }

    class NominationReceipt {
        <<record>>
        String reference
        String submittedAt
    }

    class NominationSubmissionRequest {
        <<record>>
        String nomineeEmail
        String what
        String how
        String categoryId
    }

    class LoginRequest {
        <<record>>
        String email
        String password
    }

    class ReviewDecisionRequest {
        <<record>>
        ReviewStatus status
    }

    class FavouriteRequest {
        <<record>>
        boolean favourite
    }

    class FlagResult {
        <<record>>
        String tagName
        String reasoning
    }

    class ClaudeReviewResult {
        <<record>>
        boolean isValidNomination
        boolean isVersion1Values
    }

    class ReviewStatus {
        <<enumeration>>
        PENDING
        ACCEPTED
        REJECTED
    }

    NominationView --> FlagResult
    NominationView --> ClaudeReviewResult
    NominationView --> ReviewStatus
    NominationView --> PersonSummary
    ReviewDecisionRequest --> ReviewStatus
```
