package com.example.tagging.review;

import com.example.tagging.claude.ClaudeNominationReviewer;
import com.example.tagging.claude.ClaudeReviewResult;
import com.example.tagging.nomination.Nomination;
import com.example.tagging.nomination.NominationReceipt;
import com.example.tagging.nomination.NominationStore;
import com.example.tagging.nomination.NominationSubmissionRequest;
import com.example.tagging.nomination.NominationView;
import com.example.tagging.flagging.TaggingService;
import com.example.tagging.user.UserAccountRepository;
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

    public NominationReviewService(NominationStore nominations, ReviewStateStore reviewState,
            TaggingService taggingService, ClaudeNominationReviewer claudeReviewer, UserAccountRepository users) {
        this.nominations = nominations;
        this.reviewState = reviewState;
        this.taggingService = taggingService;
        this.claudeReviewer = claudeReviewer;
        this.users = users;
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
                taggingService.evaluate(nomination, allNominations),
                reviewState.claudeReviewOf(nomination.id()).orElse(null),
                reviewState.statusOf(nomination.id()),
                reviewState.isFavourite(nomination.id()),
                profileOf(nomination.nominatorEmail()),
                profileOf(nomination.nomineeEmail()));
    }

    private PersonSummary profileOf(String email) {
        return users.findByEmailAddressIgnoreCase(email).map(PersonSummary::from).orElse(null);
    }

    private Nomination require(int id) {
        return nominations.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No nomination found with id " + id));
    }
}
