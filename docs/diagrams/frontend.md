# Angular Frontend — Models & Services

*Part of the [Star Awards class diagrams](README.md).*

The data shapes are TypeScript interfaces, kept in step with the Java records on the backend. The four services are the app's only stateful classes — each wraps one slice of the API.

```mermaid
classDiagram
    class AppUser {
        <<interface>>
        String name
        String email
        String role
    }

    class NominationView {
        <<interface>>
        number id
        String category
        FlagResult[] flags
        ClaudeReviewResult claudeReview
        ReviewStatus status
        boolean favourite
    }

    class FlagResult {
        <<interface>>
        String tagName
        String reasoning
    }

    class ClaudeReviewResult {
        <<interface>>
        boolean isValidNomination
        boolean isVersion1Values
    }

    class PersonSummary {
        <<interface>>
        String jobTitle
        String department
        String contractType
    }

    class ReviewStatus {
        <<type>>
        PENDING
        ACCEPTED
        REJECTED
    }

    class AuthService {
        <<service>>
    }
    class NominationService {
        <<service>>
    }
    class ReviewService {
        <<service>>
    }
    class UserDirectoryService {
        <<service>>
    }

    AuthService --> AppUser
    ReviewService --> NominationView
    NominationView --> FlagResult
    NominationView --> ClaudeReviewResult
    NominationView --> ReviewStatus
    NominationView --> PersonSummary
```
