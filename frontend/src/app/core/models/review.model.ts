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

/**
 * Account context for one side of a nomination — enough for the reviewer to
 * understand who the person actually is beyond just their name. Sourced from
 * the `users` collection, joined in by the backend at read time.
 */
export interface PersonSummary {
  contractType: 'PERMANENT' | 'CONTRACTOR';
  company: string;
  department: string;
  jobTitle: string;
  location: string;
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

  /** Null if the account can't be found (shouldn't happen for new submissions). */
  nominatorProfile: PersonSummary | null;
  nomineeProfile: PersonSummary | null;
}
