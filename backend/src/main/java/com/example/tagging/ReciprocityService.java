package com.example.tagging;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Computes the two "history" stats on the reviewer detail panel:
 * reciprocity and past nominations. See
 * {@code docs/nomination-detail-data-design.md} for the reasoning.
 *
 * Both look backwards from a nomination's own {@link Quarter}, not from
 * "today" - so what a reviewer sees stays the same however long after
 * submission they look at it.
 */
@Service
public class ReciprocityService {

    /** The nomination's own quarter, plus this many before it. */
    private static final int WINDOW_SIZE = 3;

    /** Nominator + nominee, plus at most this many more mutually-connected people. */
    private static final int MAX_GROUP_SIZE = 4;

    /**
     * What fraction of the possible directed nominator/nominee pairs within
     * the nominee's small reciprocal circle actually happened, as a 0-100
     * percentage. A plain pair who nominated each other scores 100; a nomination
     * with no reciprocal pattern at all scores 0.
     */
    public int reciprocityPercent(Nomination target, List<Nomination> allNominations) {
        List<Nomination> inWindow = withinWindow(target, allNominations);
        Set<String> group = buildGroup(target, inWindow);

        long possiblePairs = (long) group.size() * (group.size() - 1);
        if (possiblePairs == 0) {
            return 0;
        }

        long realizedPairs = group.stream()
                .flatMap(a -> group.stream().filter(b -> !b.equals(a)).map(b -> a + "->" + b))
                .filter(pair -> {
                    String[] parts = pair.split("->", 2);
                    return nominated(parts[0], parts[1], inWindow);
                })
                .count();

        // The nominator->nominee pair is always "realized" - it's literally the
        // nomination being scored - so it's excluded from both counts. Left in,
        // it put a 50% floor under every single nomination regardless of
        // whether anyone actually reciprocated: a plain one-way nomination with
        // no group beyond its own pair (possiblePairs == 2) would otherwise
        // always score 1 realized out of 2 possible, never the 0% the class doc
        // above promises for "no reciprocal pattern at all".
        return (int) Math.round((realizedPairs - 1) * 100.0 / (possiblePairs - 1));
    }

    /** How many other times this nominee has been nominated, any quarter, any status. */
    public int pastNominationsCount(Nomination target, List<Nomination> allNominations) {
        return personHistory(target.nomineeEmail(), target.id(), allNominations).size();
    }

    /**
     * Every other nomination touching this person - either as nominator or as
     * nominee - most recent first. Replaces the old nominator-only/nominee-only
     * split so the detail panel can show a person's full history, not just
     * half of it.
     */
    public List<Nomination> personHistory(String personEmail, int excludeId, List<Nomination> allNominations) {
        return allNominations.stream()
                .filter(other -> other.id() != excludeId)
                .filter(other -> other.nominatorEmail().equalsIgnoreCase(personEmail)
                        || other.nomineeEmail().equalsIgnoreCase(personEmail))
                .sorted(Comparator.comparing(Nomination::timestamp).reversed())
                .toList();
    }

    /**
     * Whether this pair have nominated each other at some point - any quarter,
     * any status, no time window - including the nomination currently being
     * reviewed. Deliberately simpler than {@link #reciprocityPercent}: that
     * stat scores a small time-windowed group, this just answers "did these
     * two ever nominate each other" for highlighting one history row.
     */
    public boolean reciprocalPair(String aEmail, String bEmail, List<Nomination> allNominations) {
        return nominated(aEmail, bEmail, allNominations) && nominated(bEmail, aEmail, allNominations);
    }

    private List<Nomination> withinWindow(Nomination target, List<Nomination> allNominations) {
        Quarter latest = Quarter.parse(target.quarter());
        Quarter oneBack = latest.previous();
        Quarter twoBack = oneBack.previous();
        Set<Quarter> window = Set.of(latest, oneBack, twoBack);

        return allNominations.stream()
                .filter(n -> window.contains(Quarter.parse(n.quarter())))
                .toList();
    }

    /**
     * The nominator and nominee, plus up to {@code MAX_GROUP_SIZE - 2} more
     * people who share a mutual (both-directions) nomination with someone
     * already in the group - a small reciprocal circle, not "everyone who ever
     * nominated anyone."
     */
    private Set<String> buildGroup(Nomination target, List<Nomination> inWindow) {
        Set<String> group = new LinkedHashSet<>();
        group.add(target.nominatorEmail().toLowerCase());
        group.add(target.nomineeEmail().toLowerCase());

        List<String> everyone = inWindow.stream()
                .flatMap(n -> Stream.of(n.nominatorEmail().toLowerCase(), n.nomineeEmail().toLowerCase()))
                .distinct()
                .toList();

        for (String candidate : everyone) {
            if (group.size() >= MAX_GROUP_SIZE) {
                break;
            }
            if (!group.contains(candidate) && group.stream().anyMatch(member -> mutual(member, candidate, inWindow))) {
                group.add(candidate);
            }
        }

        return group;
    }

    private boolean mutual(String a, String b, List<Nomination> inWindow) {
        return nominated(a, b, inWindow) && nominated(b, a, inWindow);
    }

    private boolean nominated(String nominatorEmail, String nomineeEmail, List<Nomination> inWindow) {
        return inWindow.stream().anyMatch(n -> n.nominatorEmail().equalsIgnoreCase(nominatorEmail)
                && n.nomineeEmail().equalsIgnoreCase(nomineeEmail));
    }
}
