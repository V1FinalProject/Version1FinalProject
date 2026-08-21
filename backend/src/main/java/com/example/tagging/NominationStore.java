package com.example.tagging;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The nominations, persisted in MongoDB (Atlas).
 *
 * Seeded once from the mock spreadsheet the first time the {@code nominations}
 * collection is empty, so the reviewer dashboard has a realistic dataset to
 * start with, then appended to as people submit through the form. Restarting
 * the app no longer re-seeds - once real submissions (or hand-loaded test
 * data) are in Mongo, the spreadsheet is only a fallback for a fresh database.
 */
@Component
public class NominationStore {

    private static final Logger log = LoggerFactory.getLogger(NominationStore.class);

    private final NominationExcelLoader seedLoader;
    private final NominationMongoRepository repository;
    private final UserAccountRepository users;
    private final AtomicInteger nextId = new AtomicInteger(1);

    public NominationStore(NominationExcelLoader seedLoader, NominationMongoRepository repository,
            UserAccountRepository users) {
        this.seedLoader = seedLoader;
        this.repository = repository;
        this.users = users;
    }

    @PostConstruct
    void seed() {
        if (repository.count() == 0) {
            try {
                List<Nomination> seedRows = seedLoader.loadAll();
                repository.saveAll(seedRows);
                log.info("Seeded {} nominations from the mock spreadsheet.", seedRows.size());
            } catch (RuntimeException e) {
                // A missing spreadsheet shouldn't stop the app - submissions still work.
                log.warn("Could not seed nominations from the spreadsheet, starting empty: {}", e.getMessage());
            }
        }

        nextId.set(repository.findFirstByOrderByIdDesc().map(Nomination::id).orElse(0) + 1);
    }

    public List<Nomination> findAll() {
        return repository.findAll();
    }

    public Optional<Nomination> findById(int id) {
        return repository.findById(id);
    }

    /** Adds a nomination, assigning it the next free id. */
    public Nomination add(NominationSubmissionRequest request) {
        request.validate();
        requireNotCoordinator(request.nomineeEmail());
        Nomination nomination = request.toNomination(nextId.getAndIncrement());
        return repository.insert(nomination);
    }

    /**
     * The reviewer/coordinator account is a shared role account, not a person -
     * it has no nominee of its own. Only blocks a match against a real account;
     * an unrecognised nominee email is allowed through unchanged; nomination
     * submission doesn't require an existing account otherwise.
     */
    private void requireNotCoordinator(String nomineeEmail) {
        boolean isCoordinator = users.findByEmailAddressIgnoreCase(nomineeEmail)
                .map(account -> account.getRole() == AccountRole.COORDINATOR)
                .orElse(false);

        if (isCoordinator) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The review coordinator can't be nominated");
        }
    }
}
