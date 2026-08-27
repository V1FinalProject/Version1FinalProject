package com.example.tagging.nomination;

import com.example.tagging.review.NominationReviewService;
import com.example.tagging.review.ReviewDecisionRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The nomination API: submitting one from the form, and everything the reviewer
 * dashboard does with the results.
 */
@RestController
@RequestMapping("/api/nominations")
public class NominationController {

    private final NominationReviewService reviewService;

    public NominationController(NominationReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /** Every nomination with its flags, Claude verdict and review state. */
    @GetMapping
    public List<NominationView> list() {
        return reviewService.findAll();
    }

    @PostMapping
    public NominationReceipt submit(@RequestBody NominationSubmissionRequest request) {
        return reviewService.submit(request);
    }

    @PutMapping("/{id}/decision")
    public NominationView decide(@PathVariable int id, @RequestBody ReviewDecisionRequest request) {
        return reviewService.decide(id, request.status());
    }

    @PutMapping("/{id}/favourite")
    public NominationView favourite(@PathVariable int id, @RequestBody FavouriteRequest request) {
        return reviewService.setFavourite(id, request.favourite());
    }

    /** Logs "Mark voucher sent" being ticked or unticked. Nothing is persisted beyond the audit entry. */
    @PutMapping("/{id}/voucher-sent")
    public void voucherSent(@PathVariable int id, @RequestBody VoucherSentRequest request) {
        reviewService.logVoucherSent(id, request.sent());
    }
}
