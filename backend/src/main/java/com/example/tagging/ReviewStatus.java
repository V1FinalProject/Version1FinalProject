package com.example.tagging;

/**
 * Where a nomination sits in the reviewer's workflow. The reviewer has the
 * final say - the flags and Claude's verdict are advisory only, so a nomination
 * can be accepted with flags against it or rejected with none.
 */
public enum ReviewStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
