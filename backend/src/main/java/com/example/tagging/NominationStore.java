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
        requireNominatableAccount(request.nominatorEmail(), "Nominator");
        requireNominatableAccount(request.nomineeEmail(), "Nominee");

        Nomination nomination = request.toNomination(nextId.getAndIncrement());
        return repository.insert(nomination);
    }

    /**
     * Both people in a nomination must be real, permanent Version 1 accounts -
     * contractors can't submit or receive a Star Award. The frontend already
     * keeps contractors off the nominee picker and out of the form, so this is
     * the same defense-in-depth as the self-nomination check: the real
     * enforcement is here, the UI is just the first line of it.
     */
    private void requireNominatableAccount(String email, String role) {
        UserAccount account = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> badRequest(role + " must be an existing Version 1 account"));

        if (account.getContractType() == ContractType.CONTRACTOR) {
            throw badRequest("Contractors cannot submit or receive Star Award nominations");
        }
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
