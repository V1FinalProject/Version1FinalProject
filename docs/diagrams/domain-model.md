# Persisted Domain Model

*Part of the [Star Awards class diagrams](README.md).*

The three things stored in MongoDB — a nomination, a user account, and the reviewer's decision on that nomination — plus the small value types and enums they're built from.

```mermaid
classDiagram
    class Nomination {
        <<record>>
        int id
        String nominatorEmail
        String nomineeEmail
        String category
        String justification
    }

    class UserAccount {
        String emailAddress
        String firstName
        String lastName
        AccountRole role
        String department
    }

    class ReviewStateDocument {
        int nominationId
        ReviewStatus status
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

    class AccountRole {
        <<enumeration>>
        EMPLOYEE
        COORDINATOR
    }

    class ContractType {
        <<enumeration>>
        PERMANENT
        CONTRACTOR
    }

    class ReviewStatus {
        <<enumeration>>
        PENDING
        ACCEPTED
        REJECTED
    }

    class NominationCategory {
        <<enumeration>>
        COLLABORATION_AND_ENGAGEMENT
        CUSTOMER_IMPACT
        INNOVATION_AND_GROWTH
        PERFORMANCE_AND_EFFICIENCY
        QUALITY_AND_COMPLIANCE
    }

    UserAccount --> AccountRole
    UserAccount --> ContractType
    ReviewStateDocument --> ReviewStatus
    ReviewStateDocument --> ClaudeReviewResult
    Nomination --> NominationCategory
```

`Nomination` and `ReviewStateDocument` are separate documents in separate collections, joined by nomination id — the nomination itself never changes once submitted, only the reviewer's decision does.
