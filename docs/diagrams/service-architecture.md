# Service, Controller & Repository Architecture

*Part of the [Star Awards class diagrams](README.md).*

The backend wiring: controllers receive HTTP requests, `NominationReviewService` orchestrates the work, and everything ultimately reads or writes through a Spring Data repository.

```mermaid
classDiagram
    class AuthController {
        <<controller>>
    }
    class UserDirectoryController {
        <<controller>>
    }
    class NominationController {
        <<controller>>
    }
    class NominationReviewController {
        <<controller>>
    }

    class NominationReviewService {
        <<service>>
    }
    class NominationStore {
        <<service>>
    }
    class ReviewStateStore {
        <<service>>
    }
    class TaggingService {
        <<service>>
    }
    class ClaudeNominationReviewer {
        <<service>>
    }

    class NominationMongoRepository {
        <<repository>>
    }
    class UserAccountRepository {
        <<repository>>
    }
    class ReviewStateMongoRepository {
        <<repository>>
    }
    class SpringDataMongoRepository {
        <<interface>>
    }

    AuthController --> UserAccountRepository
    UserDirectoryController --> UserAccountRepository
    NominationController --> NominationReviewService
    NominationReviewController --> NominationReviewService

    NominationReviewService --> NominationStore
    NominationReviewService --> ReviewStateStore
    NominationReviewService --> TaggingService
    NominationReviewService --> ClaudeNominationReviewer
    NominationReviewService --> UserAccountRepository

    NominationStore --> NominationMongoRepository
    NominationStore --> UserAccountRepository
    ReviewStateStore --> ReviewStateMongoRepository

    NominationMongoRepository --|> SpringDataMongoRepository
    UserAccountRepository --|> SpringDataMongoRepository
    ReviewStateMongoRepository --|> SpringDataMongoRepository
```

Supporting classes not shown for clarity: `NominationExcelLoader` (one-time spreadsheet seed), `UserAccountSeeder` (one-time demo accounts), `TaggingCliRunner` (a dev-only console tool), and the two `@Configuration` classes that provide the `AnthropicClient` and `PasswordEncoder` beans.
