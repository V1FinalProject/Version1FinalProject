/**
 * The signed-in colleague.
 *
 * The case study requires that "nominator identity is captured automatically",
 * so every nomination is stamped with the fields below rather than asking the
 * nominator to type them. `practice` and `location` exist because the reviewer
 * dashboard filters on them.
 *
 * This shape is what the backend should return from the real login endpoint —
 * the demo users below stand in until that exists.
 */
export interface AppUser {
  id: string;
  name: string;
  email: string;
  practice: string;
  location: string;
  role: 'employee' | 'coordinator';
}

/**
 * Fake accounts for the login screen. Any non-empty password is accepted.
 *
 * These are invented people, but shaped like real records so the backend team
 * can seed the same rows into the MySQL `users` table. Delete this list once
 * real authentication is wired up.
 */
export const DEMO_USERS: readonly AppUser[] = [
  {
    id: 'u-001',
    name: 'Joe Duffy',
    email: 'joe.duffy@version1.com',
    practice: 'Digital, Data & Cloud',
    location: 'Dublin',
    role: 'employee',
  },
  {
    id: 'u-002',
    name: 'Niamh Gallagher',
    email: 'niamh.gallagher@version1.com',
    practice: 'Enterprise Applications',
    location: 'Belfast',
    role: 'employee',
  },
  {
    id: 'u-003',
    name: 'Rory McKenna',
    email: 'rory.mckenna@version1.com',
    practice: 'Managed Services',
    location: 'Birmingham',
    role: 'employee',
  },
  {
    id: 'u-004',
    name: 'Aoife Byrne',
    email: 'aoife.byrne@version1.com',
    practice: 'Consulting & Advisory',
    location: 'Cork',
    role: 'employee',
  },
  {
    // The coordinator account. This is the one that will gate the review
    // dashboard once that exists — nominating is open to everyone, reviewing
    // is not.
    id: 'u-005',
    name: 'Star Award Reviewer',
    email: 'reviewer@version1.com',
    practice: 'People & Culture',
    location: 'Dublin',
    role: 'coordinator',
  },
];
