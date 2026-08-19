/**
 * The nomination round this form is collecting for.
 *
 * Not derived from the date: the programme's quarters don't line up with
 * calendar quarters (the live Microsoft Form said "Q3 2026" in August 2026), so
 * this is set by hand each round. It's sent with the submission because the
 * reviewer dashboard needs it to spot repeat nominations in consecutive
 * quarters.
 */
export const CURRENT_QUARTER = 'Q4 2026';

/** Programme guidelines, linked from the banner. */
export const PROGRAMME_URL = 'https://version1.sharepoint.com/sites/star-award';

/**
 * Nominees must be colleagues, so only this email domain is accepted.
 *
 * The login side is already closed by construction — it only matches against a
 * fixed list of accounts — but the nominee field is free text, so it needs an
 * explicit check.
 */
export const ALLOWED_EMAIL_DOMAIN = 'version1.com';

/** Character limits applied to the WHAT and HOW answers individually. */
export const NARRATIVE_MAX_LENGTH = 700;
export const NARRATIVE_MIN_LENGTH = 50;

/**
 * Version 1's Core Values. Shown as chips beside the HOW question — a
 * nomination must demonstrate at least one of them.
 */
export const CORE_VALUES: readonly string[] = [
  'Honesty & Integrity',
  'Excellence',
  'Drive',
  'No Ego',
  'Customer First',
  'Personal Commitment',
];

export interface NominationCategory {
  /** Stored in the database and sent to the API. */
  id: string;
  label: string;
  /**
   * Comma-separated examples, rendered after an "Examples:" label in the
   * card tooltip and in the panel below the grid.
   */
  examples: string;
}

/** The five nomination categories. */
export const NOMINATION_CATEGORIES: readonly NominationCategory[] = [
  {
    id: 'collaboration-and-engagement',
    label: 'Collaboration & Engagement',
    // TODO: placeholder — this is the one category we don't have official
    // examples for yet. Replace with the programme's wording.
    examples: 'cross-team collaboration, mentoring, initiatives that lifted engagement',
  },
  {
    id: 'customer-impact',
    label: 'Customer Impact',
    examples: 'increased customer satisfaction, time improvements, retention uplift',
  },
  {
    id: 'innovation-and-growth',
    label: 'Innovation & Growth',
    examples: 'new ideas implemented, blockers removed, scalable solution introduced',
  },
  {
    id: 'performance-and-efficiency',
    label: 'Performance & Efficiency',
    examples: 'process time reduced, tasks automated, increased throughput',
  },
  {
    id: 'quality-and-compliance',
    label: 'Quality & Compliance',
    examples: 'error reduction %, improved accuracy, compliance checks completed',
  },
];

/**
 * The payload sent to `POST {apiBaseUrl}/nominations`.
 *
 * `what` and `how` are the two halves of "why are you nominating this person",
 * matching the case study's WHAT/HOW split. The nominator block is captured
 * automatically from the signed-in user — never typed — and practice/location
 * are included because the reviewer dashboard filters on them.
 */
export interface NominationSubmission {
  nomineeName: string;
  nomineeEmail: string;
  /** What the nominee did, the impact, who was affected, supporting evidence. */
  what: string;
  /** How they demonstrated at least one Core Value. */
  how: string;
  categoryId: string;
  /** Nominator opted in to an email copy of their answers. */
  emailReceipt: boolean;

  nominatorId: string;
  nominatorName: string;
  nominatorEmail: string;
  practice: string;
  location: string;

  /** e.g. `Q4 2026` — the nomination round. */
  quarter: string;
  /** ISO 8601, UTC. */
  submittedAt: string;
}

/** What the API returns once a nomination is stored. */
export interface NominationReceipt {
  /** Human-readable reference shown to the nominator, e.g. `SA-2026-0042`. */
  reference: string;
  submittedAt: string;
}
