package com.example.tagging;

import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for a single nomination tagging check. Each implementation
 * covers exactly one tag.
 */
public interface NominationFlagChecker {

    Optional<FlagResult> check(Nomination target, List<Nomination> allNominations);
}
