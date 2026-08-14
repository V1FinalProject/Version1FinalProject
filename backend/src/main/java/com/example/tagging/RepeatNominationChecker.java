package com.example.tagging;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Flags a nomination if the same nominee was also nominated in the immediately
// preceding quarter (Q1 = Jan-Mar, Q2 = Apr-Jun, Q3 = Jul-Sep, Q4 = Oct-Dec).
// Structural, rule-based check: only nominee email and timestamp quarter matter.
@Component
public class RepeatNominationChecker implements NominationFlagChecker {

    @Override
    public Optional<FlagResult> check(Nomination target, List<Nomination> allNominations) {
        Quarter precedingQuarter = Quarter.of(target.timestamp()).previous();

        Optional<Nomination> priorNomination = allNominations.stream()
                .filter(other -> other.id() != target.id())
                .filter(other -> other.nomineeEmail().equalsIgnoreCase(target.nomineeEmail()))
                .filter(other -> Quarter.of(other.timestamp()).equals(precedingQuarter))
                .findFirst();

        return priorNomination.map(prior -> new FlagResult(
                "Repeat Nomination",
                "Same nominee (" + target.nomineeName() + ") was also nominated in " + precedingQuarter
                        + " by nomination #" + prior.id()));
    }

    private record Quarter(int year, int quarterNumber) {

        static Quarter of(LocalDateTime timestamp) {
            int quarterNumber = ((timestamp.getMonthValue() - 1) / 3) + 1;
            return new Quarter(timestamp.getYear(), quarterNumber);
        }

        Quarter previous() {
            return quarterNumber == 1 ? new Quarter(year - 1, 4) : new Quarter(year, quarterNumber - 1);
        }

        @Override
        public String toString() {
            return "Q" + quarterNumber + " " + year;
        }
    }
}
