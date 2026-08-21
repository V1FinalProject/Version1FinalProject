/**
 * The signed-in colleague.
 *
 * The case study requires that "nominator identity is captured automatically",
 * so every nomination is stamped with the fields below rather than asking the
 * nominator to type them. `practice` and `location` exist because the reviewer
 * dashboard filters on them, and map onto the account's `department`/
 * `workLocation` on the backend.
 *
 * Returned by the real login endpoint (`POST /api/auth/login`) - see
 * `AuthService`.
 */
export interface AppUser {
  id: string;
  name: string;
  email: string;
  practice: string;
  location: string;
  role: 'employee' | 'coordinator';
}
