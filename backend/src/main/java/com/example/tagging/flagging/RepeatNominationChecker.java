package com.example.tagging.flagging;

import com.example.tagging.nomination.Quarter;
import com.example.tagging.nomination.Nomination;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// Flags a nomination if the same nominee was also nominated in the
// immediately preceding programme quarter (Nomination.quarter(), not a
// calendar quarter - see Quarter).
@Component
public class RepeatNominationChecker implements NominationFlagChecker {

    @Override
    public Optional<FlagResult> check(Nomination target, List<Nomination> allNominations) {
        Quarter precedingQuarter = Quarter.parse(target.quarter()).previous();

        Optional<Nomination> priorNomination = allNominations.stream()
                .filter(other -> other.id() != target.id())
                .filter(other -> other.nomineeEmail().equalsIgnoreCase(target.nomineeEmail()))
                .filter(other -> Quarter.parse(other.quarter()).equals(precedingQuarter))
                .findFirst();

        return priorNomination.map(prior -> new FlagResult(
                "Repeat Nomination",
                "Same nominee (" + target.nomineeName() + ") was also nominated in " + precedingQuarter
                        + " by nomination #" + prior.id()));
    }
}
