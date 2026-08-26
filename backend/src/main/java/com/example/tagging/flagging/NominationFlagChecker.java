package com.example.tagging.flagging;

import com.example.tagging.nomination.Nomination;
import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for a single nomination tagging check. Each implementation
 * covers exactly one tag.
 */
public interface NominationFlagChecker {

    Optional<FlagResult> check(Nomination target, List<Nomination> allNominations);
}
