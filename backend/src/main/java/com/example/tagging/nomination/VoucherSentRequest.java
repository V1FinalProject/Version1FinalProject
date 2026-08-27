package com.example.tagging.nomination;

/**
 * Body of PUT /api/nominations/{id}/voucher-sent.
 *
 * There's no persisted voucher-sent field yet (see {@code Review}'s
 * {@code voucherSentIds} on the frontend) - this only records the action in
 * the audit log.
 */
public record VoucherSentRequest(boolean sent) {
}
