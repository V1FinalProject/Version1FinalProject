package com.example.tagging;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// Flags a nomination where the nominator and nominee are the same person, matching
// on either email or name in case one of the two fields is inconsistent in the data.
@Component
public class SelfNominationChecker implements NominationFlagChecker {

    @Override
    public Optional<FlagResult> check(Nomination target, List<Nomination> allNominations) {
        boolean sameEmail = target.nominatorEmail().equalsIgnoreCase(target.nomineeEmail());
        boolean sameName = target.nominatorName().equalsIgnoreCase(target.nomineeName());

        if (!sameEmail && !sameName) {
            return Optional.empty();
        }

        return Optional.of(new FlagResult(
                "Self Nomination",
                target.nominatorName() + " nominated themselves"));
    }
}
