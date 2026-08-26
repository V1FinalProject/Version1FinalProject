package com.example.tagging.org;

import com.example.tagging.nomination.NominationStore;
import com.example.tagging.user.UserAccountSeeder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Seeds the {@code org_units} collection once, the first time it's empty -
 * same pattern as {@link UserAccountSeeder} and {@link NominationStore}.
 *
 * Reads {@code seed-data/org-units.json}, a hand-authored list of
 * division/department headcounts (see
 * {@code docs/nomination-detail-data-design.md}).
 */
@Component
public class OrgUnitSizeSeeder {

    private static final Logger log = LoggerFactory.getLogger(OrgUnitSizeSeeder.class);

    private final OrgUnitSizeRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrgUnitSizeSeeder(OrgUnitSizeRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    void seed() {
        if (repository.count() > 0) {
            return;
        }

        try {
            List<Row> rows = mapper.readValue(
                    new ClassPathResource("seed-data/org-units.json").getInputStream(),
                    mapper.getTypeFactory().constructCollectionType(List.class, Row.class));

            List<OrgUnitSize> units = rows.stream()
                    .map(row -> new OrgUnitSize(row.name(), OrgUnitType.valueOf(row.type()), row.size()))
                    .toList();

            repository.saveAll(units);
            log.info("Seeded {} org units.", units.size());
        } catch (IOException e) {
            // Same tolerance as NominationStore's spreadsheet seed - a missing
            // resource shouldn't stop the app, it just leaves team/company size blank.
            log.warn("Could not seed org units: {}", e.getMessage());
        }
    }

    private record Row(String name, String type, int size) {
    }
}
