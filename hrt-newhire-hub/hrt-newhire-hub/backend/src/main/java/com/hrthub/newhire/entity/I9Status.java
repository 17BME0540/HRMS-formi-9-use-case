package com.hrthub.newhire.entity;

/**
 * Mirrors the coarse-grained statuses returned by a real I-9 Tracker style
 * integration: verification is either still in flight, cleared, or flagged
 * for manual HR follow-up.
 */
public enum I9Status {
    PENDING,
    VERIFIED,
    REJECTED
}
