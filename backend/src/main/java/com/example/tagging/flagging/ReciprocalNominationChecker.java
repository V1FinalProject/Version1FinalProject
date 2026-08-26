package com.example.tagging.flagging;

import com.example.tagging.nomination.Nomination;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// Flags a nomination if the nominee also nominated the nominator back, i.e. an
// inverse pair exists elsewhere in the dataset. Structural, rule-based check
// based purely on nominator/nominee email pairs.
@Component
public class ReciprocalNominationChecker implements NominationFlagChecker {

    @Override
    public Optional<FlagResult> check(Nomination target, List<Nomination> allNominations) {
        Optional<Nomination> reciprocalNomination = allNominations.stream()
                .filter(other -> other.id() != target.id())
                .filter(other -> other.nominatorEmail().equalsIgnoreCase(target.nomineeEmail()))
                .filter(other -> other.nomineeEmail().equalsIgnoreCase(target.nominatorEmail()))
                .findFirst();

        return reciprocalNomination.map(other -> new FlagResult(
                "Reciprocal Nomination",
                target.nomineeName() + " nominated " + target.nominatorName()
                        + " back in nomination #" + other.id()));
    }
}
