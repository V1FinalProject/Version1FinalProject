package com.example.tagging;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The nominations, held in memory.
 *
 * Seeded once at startup from the mock spreadsheet so the reviewer dashboard has
 * a realistic dataset to work with, then appended to as people submit through
 * the form. Everything is lost on restart - this is the stand-in for the MySQL
 * table that replaces it later, at which point only this class changes.
 */
@Component
public class NominationStore {

    private static final Logger log = LoggerFactory.getLogger(NominationStore.class);

    private final NominationExcelLoader seedLoader;
    private final List<Nomination> nominations = new CopyOnWriteArrayList<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public NominationStore(NominationExcelLoader seedLoader) {
        this.seedLoader = seedLoader;
    }

    @PostConstruct
    void seed() {
        try {
            List<Nomination> seedRows = seedLoader.loadAll();
            nominations.addAll(seedRows);
            nextId.set(seedRows.stream().mapToInt(Nomination::id).max().orElse(0) + 1);
            log.info("Seeded {} nominations from the mock spreadsheet.", seedRows.size());
        } catch (RuntimeException e) {
            // A missing spreadsheet shouldn't stop the app - submissions still work.
            log.warn("Could not seed nominations from the spreadsheet, starting empty: {}", e.getMessage());
        }
    }

    public List<Nomination> findAll() {
        return List.copyOf(nominations);
    }

    public Optional<Nomination> findById(int id) {
        return nominations.stream().filter(nomination -> nomination.id() == id).findFirst();
    }

    /** Adds a nomination, assigning it the next free id. */
    public Nomination add(NominationSubmissionRequest request) {
        request.validate();
        Nomination nomination = request.toNomination(nextId.getAndIncrement());
        nominations.add(nomination);
        return nomination;
    }
}
