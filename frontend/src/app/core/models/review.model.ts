/**
 * The reviewer dashboard's view of the API.
 *
 * These mirror the backend records in `com.example.tagging` one-for-one —
 * `NominationView`, `FlagResult`, `ClaudeReviewResult` and `ReviewStatus`. Keep
 * them in step when the Java side changes.
 */

/** Where the reviewer has left a nomination. Everything starts PENDING. */
export type ReviewStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

/** One tag the rule-based checkers raised, with the reasoning behind it. */
export interface FlagResult {
  tagName: string;
  reasoning: string;
}

/** Claude's verdict on a nomination. */
export interface ClaudeReviewResult {
  /** False for spam, gibberish, jokes — anything that isn't a real nomination. */
  isValidNomination: boolean;
  /** Whether the justification actually demonstrates a Version 1 core value. */
  isVersion1Values: boolean;
}

/** One row of the dashboard. */
export interface NominationView {
  id: number;
  /** ISO 8601, no zone — the backend stores UTC. */
  timestamp: string;

  nominatorName: string;
  nominatorEmail: string;
  nomineeName: string;
  nomineeEmail: string;

  /** Display label, e.g. `Customer Impact`. */
  category: string;
  what: string;
  how: string;
  /** WHAT and HOW joined — what the flag checkers and Claude actually read. */
  justification: string;

  /** Null on the seeded nominations, which predate these fields. */
  practice: string | null;
  location: string | null;

  flags: FlagResult[];
  /** Null until the nomination has been sent to Claude. */
  claudeReview: ClaudeReviewResult | null;
  status: ReviewStatus;
  favourite: boolean;
}
