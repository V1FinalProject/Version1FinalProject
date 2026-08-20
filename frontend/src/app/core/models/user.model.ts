/**
 * The signed-in colleague.
 *
 * The case study requires that "nominator identity is captured automatically",
 * so every nomination is stamped with the fields below rather than asking the
 * nominator to type them. `practice` and `location` exist because the reviewer
 * dashboard filters on them. `contractType` gates the nomination form itself —
 * contractors can't submit or receive a Star Award.
 *
 * This is exactly what `POST /api/auth/login` returns — see
 * `AuthenticatedUser` on the backend.
 */
export interface AppUser {
  id: string;
  name: string;
  email: string;
  practice: string;
  location: string;
  role: 'employee' | 'coordinator';
  contractType: 'permanent' | 'contractor';
}
