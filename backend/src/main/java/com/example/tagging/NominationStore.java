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
    private final HistoricalNominationLoader historicalLoader;
    private final NominationMongoRepository repository;
    private final UserAccountRepository users;
    private final ReviewStateStore reviewState;
    private final AtomicInteger nextId = new AtomicInteger(1);

    public NominationStore(NominationExcelLoader seedLoader, HistoricalNominationLoader historicalLoader,
            NominationMongoRepository repository, UserAccountRepository users, ReviewStateStore reviewState) {
        this.seedLoader = seedLoader;
        this.historicalLoader = historicalLoader;
        this.repository = repository;
        this.users = users;
        this.reviewState = reviewState;
    }

    @PostConstruct
    void seed() {
        if (repository.count() == 0) {
            int seededCount = 0;
            try {
                List<Nomination> seedRows = seedLoader.loadAll();
                repository.saveAll(seedRows);
                seededCount = seedRows.size();
                log.info("Seeded {} nominations from the mock spreadsheet.", seededCount);
            } catch (RuntimeException e) {
                // A missing spreadsheet shouldn't stop the app - submissions still work.
                log.warn("Could not seed nominations from the spreadsheet, starting empty: {}", e.getMessage());
            }

            seedHistorical(seededCount);
        }

        nextId.set(repository.findFirstByOrderByIdDesc().map(Nomination::id).orElse(0) + 1);
    }

    /**
     * Adds the bundled Q2/Q3 nominations after the Q4 spreadsheet seed, so
     * "past nominations" isn't empty on a fresh database - see
     * {@code docs/nomination-detail-data-design.md}. Ids continue on from
     * wherever the spreadsheet seed left off, so both batches share one
     * sequence.
     */
    private void seedHistorical(int afterCount) {
        int nextSeedId = afterCount + 1;

        for (HistoricalNominationLoader.Row row : historicalLoader.loadAll()) {
            NominationCategory category = NominationCategory.byId(row.category())
                    .orElseThrow(() -> new IllegalStateException("Unknown categoryId in seed data: " + row.category()));

            Nomination nomination = Nomination.fromJustification(nextSeedId++, row.timestamp(), row.nominatorName(),
                    row.nominatorEmail(), row.nomineeName(), row.nomineeEmail(), row.text(), category.label(),
                    row.quarter());
            repository.insert(nomination);
            reviewState.setStatus(nomination.id(), ReviewStatus.valueOf(row.status()));
        }

        log.info("Seeded {} historical nominations (Q2/Q3).", nextSeedId - afterCount - 1);
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
     * Both people in a nomination must be real Version 1 accounts - the
     * frontend already keeps the nominee picker to real, matched colleagues,
     * so this is the real enforcement behind that, not the primary guard. The
     * nominee additionally can't be the coordinator account - it's a shared
     * role account, not a person, so it has no nominee of its own.
     */
    private void requireNominatableAccount(String email, String role) {
        UserAccount account = users.findByEmailAddressIgnoreCase(email)
                .orElseThrow(() -> badRequest(role + " must be an existing Version 1 account"));

        if (role.equals("Nominee") && account.getRole() == AccountRole.COORDINATOR) {
            throw badRequest("The review coordinator can't be nominated");
        }
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
