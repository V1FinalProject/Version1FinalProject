package com.example.tagging.nomination;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reads {@code seed-data/historical-nominations.json} - the Q2/Q3 nominations
 * bundled alongside the Q4 spreadsheet seed, so "past nominations" isn't
 * empty on a fresh database. See
 * {@code docs/nomination-detail-data-design.md} for where this data came
 * from (a mix of real Q3 records and hand-authored Q2 examples).
 *
 * Deliberately separate from {@link NominationExcelLoader}: different source
 * format, and this one carries a status straight from the sheet
 * ("Nomination Approved"), which the xlsx path doesn't have at all.
 */
@Component
public class HistoricalNominationLoader {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public List<Row> loadAll() {
        try {
            return mapper.readValue(
                    new ClassPathResource("seed-data/historical-nominations.json").getInputStream(),
                    mapper.getTypeFactory().constructCollectionType(List.class, Row.class));
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read historical nominations seed data", e);
        }
    }

    public record Row(String quarter, LocalDateTime timestamp, String nominatorName, String nominatorEmail,
            String nomineeName, String nomineeEmail, String text, String category, String status) {
    }
}
