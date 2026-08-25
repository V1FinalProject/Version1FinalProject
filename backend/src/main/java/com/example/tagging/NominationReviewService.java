package com.example.tagging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Everything the reviewer dashboard needs, assembled in one place: the stored
 * nominations, the rule-based flags, Claude's verdict, and the reviewer's own
 * decisions.
 *
 * The flags are recomputed on read rather than stored. They are cheap, and two
 * of them (Reciprocal, Repeat) depend on the rest of the dataset, so a flag
 * cached at submission time would go stale the moment the matching nomination
 * arrived. Claude's verdict is the opposite - it depends only on the
 * nomination's own text and costs an API call - so that one is cached.
 */
@Service
public class NominationReviewService {

    private static final Logger log = LoggerFactory.getLogger(NominationReviewService.class);

    private final NominationStore nominations;
    private final ReviewStateStore reviewState;
    private final TaggingService taggingService;
    private final ClaudeNominationReviewer claudeReviewer;
    private final UserAccountRepository users;
    private final OrgSizeLookup orgSizes;
    private final ReciprocityService reciprocity;

    public NominationReviewService(NominationStore nominations, ReviewStateStore reviewState,
            TaggingService taggingService, ClaudeNominationReviewer claudeReviewer, UserAccountRepository users,
            OrgSizeLookup orgSizes, ReciprocityService reciprocity) {
        this.nominations = nominations;
        this.reviewState = reviewState;
        this.taggingService = taggingService;
        this.claudeReviewer = claudeReviewer;
        this.users = users;
        this.orgSizes = orgSizes;
        this.reciprocity = reciprocity;
    }

    /** Every nomination, newest first, with its flags and current review state. */
    public List<NominationView> findAll() {
        List<Nomination> all = nominations.findAll();

        return all.stream()
                .map(nomination -> toView(nomination, all))
                .sorted((left, right) -> Integer.compare(right.id(), left.id()))
                .toList();
    }

    /**
     * Stores a submitted nomination and asks Claude to review it.
     *
     * The nomination is stored first, so a Claude outage costs the reviewer a
     * verdict they can re-request later rather than costing the nominator their
     * submission.
     */
    public NominationReceipt submit(NominationSubmissionRequest request) {
        Nomination stored = nominations.add(request);

        try {
            reviewState.saveClaudeReview(stored.id(), review(stored));
        } catch (ResponseStatusException e) {
            // The nomination is already stored; the reviewer can ask for a verdict later.
            log.warn("Nomination {} stored but left unreviewed.", stored.id());
        }

        return NominationReceipt.forNomination(stored);
    }

    /** One nomination with its flags and current review state. */
    public NominationView find(int id) {
        return toView(require(id), nominations.findAll());
    }

    /**
     * Claude's verdict on a nomination, reviewing it on first request and
     * serving the cached answer after that.
     */
    public ClaudeReviewResult claudeReview(int id) {
        Nomination nomination = require(id);

        return reviewState.claudeReviewOf(id).orElseGet(() -> {
            ClaudeReviewResult result = review(nomination);
            reviewState.saveClaudeReview(id, result);
            return result;
        });
    }

    /**
     * Calls Claude, translating a failed call into a 503 rather than a bare 500.
     *
     * The model API returns 529 when it is overloaded, and the SDK has already
     * retried by the time that reaches us. Nothing is wrong with the nomination
     * and asking again later usually works, so the reviewer needs to be told to
     * retry - not shown a stack trace.
     */
    private ClaudeReviewResult review(Nomination nomination) {
        try {
            return claudeReviewer.review(nomination);
        } catch (RuntimeException e) {
            log.warn("Claude review failed for nomination {}: {}", nomination.id(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Claude could not review nomination " + nomination.id() + " right now. Please try again.", e);
        }
    }

    /** Accept, reject, or move a nomination back to pending. The reviewer has the final say. */
    public NominationView decide(int id, ReviewStatus status) {
        require(id);
        reviewState.setStatus(id, status);
        return find(id);
    }

    /** Star or unstar a nomination for the reviewer's shortlist. */
    public NominationView setFavourite(int id, boolean favourite) {
        require(id);
        reviewState.setFavourite(id, favourite);
        return find(id);
    }

    private NominationView toView(Nomination nomination, List<Nomination> allNominations) {
        return new NominationView(
                nomination.id(),
                nomination.timestamp(),
                nomination.nominatorName(),
                nomination.nominatorEmail(),
                nomination.nomineeName(),
                nomination.nomineeEmail(),
                nomination.category(),
                nomination.what(),
                nomination.how(),
                nomination.justification(),
                nomination.practice(),
                nomination.location(),
                nomination.quarter(),
                taggingService.evaluate(nomination, allNominations),
                reviewState.claudeReviewOf(nomination.id()).orElse(null),
                reviewState.statusOf(nomination.id()),
                reviewState.isFavourite(nomination.id()),
                profileOf(nomination.nominatorEmail()),
                profileOf(nomination.nomineeEmail()),
                reciprocity.reciprocityPercent(nomination, allNominations),
                reciprocity.pastNominationsCount(nomination, allNominations),
                historyEntries(nomination.nominatorEmail(), nomination.nomineeEmail(), nomination.id(),
                        allNominations),
                historyEntries(nomination.nomineeEmail(), nomination.nominatorEmail(), nomination.id(),
                        allNominations));
    }

    private PersonSummary profileOf(String email) {
        return users.findByEmailAddressIgnoreCase(email).map(account -> PersonSummary.from(account, orgSizes)).orElse(null);
    }

    /**
     * Builds one person's full nomination history - both the times they
     * nominated someone and the times they were nominated. {@code otherEmail}
     * is the other person in the nomination currently being reviewed (the
     * nominee, when {@code personEmail} is the nominator, and vice versa) -
     * an entry is only flagged {@code reciprocal} when it's specifically
     * between this pair and they've nominated each other back at some point,
     * not merely because {@code personEmail} has a reciprocal history with
     * someone else entirely. See {@link NominationHistoryEntry}'s Javadoc.
     */
    private List<NominationHistoryEntry> historyEntries(String personEmail, String otherEmail, int excludeId,
            List<Nomination> allNominations) {
        boolean pairReciprocal = reciprocity.reciprocalPair(personEmail, otherEmail, allNominations);

        return reciprocity.personHistory(personEmail, excludeId, allNominations).stream()
                .map(n -> {
                    boolean outbound = n.nominatorEmail().equalsIgnoreCase(personEmail);
                    String counterpartEmail = outbound ? n.nomineeEmail() : n.nominatorEmail();
                    String counterpartName = outbound ? n.nomineeName() : n.nominatorName();
                    boolean reciprocal = pairReciprocal && counterpartEmail.equalsIgnoreCase(otherEmail);
                    return new NominationHistoryEntry(n.id(), n.quarter(), n.category(), counterpartName,
                            reviewState.statusOf(n.id()),
                            outbound ? NominationHistoryEntry.HistoryDirection.OUTBOUND
                                    : NominationHistoryEntry.HistoryDirection.INBOUND,
                            reciprocal);
                })
                .toList();
    }

    private Nomination require(int id) {
        return nominations.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No nomination found with id " + id));
    }
}
