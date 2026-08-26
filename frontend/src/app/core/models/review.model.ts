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

/**
 * One earlier nomination shown in the "previous nominations" history —
 * either something the nominator submitted before, or something the nominee
 * received before. `counterpartName` is the nominee they nominated on the
 * nominator's side, or the nominator who nominated them on the nominee's side.
 */
/** Which way this history entry ran: this person nominating, or being nominated. */
export type HistoryDirection = 'OUTBOUND' | 'INBOUND';

export interface NominationHistoryEntry {
  id: number;
  quarter: string;
  category: string;
  counterpartName: string;
  status: ReviewStatus;
  direction: HistoryDirection;
  /** True when this pair have nominated each other at some point, any quarter. */
  reciprocal: boolean;
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
  jobTitle: string;
  department: string;
  company: string;
  workLocation: string;
  contractType: string;
  /** Headcount of `department`. Null if there's no matching org_units row. */
  teamSize: number | null;
  /**
   * Headcount of the person's *division*, not a literal company field — see
   * `OrgUnitSize`'s Javadoc on the backend. Null if there's no matching row.
   */
  companySize: number | null;
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

  /** The programme round this nomination belongs to, e.g. `Q4 2026`. */
  quarter: string;

  flags: FlagResult[];
  /** Null until the nomination has been sent to Claude. */
  claudeReview: ClaudeReviewResult | null;
  status: ReviewStatus;
  favourite: boolean;

  /** Null if the account can't be found (nominations don't require one to exist). */
  nominatorProfile: PersonSummary | null;
  nomineeProfile: PersonSummary | null;

  /** 0–100. How much of the nominee's small reciprocal circle nominates back. */
  reciprocityPercent: number;
  /** How many other times this nominee has been nominated, any quarter, any status. */
  pastNominationsCount: number;
  /** Every other nomination this nominator has submitted, most recent first. */
  nominatorHistory: NominationHistoryEntry[];
  /** Every other nomination this nominee has received, most recent first. */
  nomineeHistory: NominationHistoryEntry[];
}
