# Star Awards — Frontend

Angular 22 frontend for the Star Awards recognition platform. Deployment target is
Azure (static hosting), talking to the Spring Boot API with MongoDB Atlas behind it.

Colleagues submit nominations through a guided form; a coordinator reviews them on a
dashboard, with rule-based flags and an optional Claude review verdict alongside to
help spot the ones that need a closer look. Both the form and the dashboard are built.

## Running in IntelliJ IDEA Community Edition

IDEA CE doesn't bundle the JavaScript/TypeScript or Node.js plugins (those are
Ultimate-only), so there's no Angular tool window and no npm run configurations.
Everything works through the built-in terminal:

1. **File → Open** and select the `frontend` folder.
2. Mark `node_modules`, `dist` and `.angular` as excluded if IDEA hasn't already:
   right-click → **Mark Directory as → Excluded**. Keeps indexing fast.
3. Open the terminal (`Alt+F12`), run `npm install` (first time) then `npm start`.
4. Open <http://localhost:4200/>. Saving a file hot-reloads the browser.

For a one-click run button, add a **Shell Script** run configuration with *Script
text* `npm start` — those are available in CE.

The dev server proxies `/api` to the Spring Boot app on `localhost:8080`
(`proxy.conf.json`), so run the backend alongside it — the login screen, nominee
picker and dashboard all need it. `useMockApi` in `environment.ts` is `false`; there's
no working app with the backend stopped.

## Scripts

| Command | What it does |
| --- | --- |
| `npm start` | Dev server on <http://localhost:4200/> with hot reload |
| `npm run build` | Production build into `dist/` |
| `npm run watch` | Development build, rebuilding on change |
| `npm test` | Unit tests (Vitest) |

## Routes

| Route | Screen | Who can reach it |
| --- | --- | --- |
| `/login` | Sign-in, against the real `users` collection via `POST /api/auth/login` | Anyone |
| `/nominate` | The nomination form | Signed-in employees |
| `/review` | Reviewer dashboard — table, filters, flags, Claude review, shortlist | Coordinators only |
| `/` | Redirects by role — coordinators to `/review`, everyone else to `/nominate` | — |

`AppUser.role` is either `employee` or `coordinator`, and the two are mutually
exclusive: employees nominate and can't reach the dashboard; coordinators review and
can't nominate, since a coordinator who nominated would review their own submission.
Enforced by `core/guards/role.guard.ts` — a UX guard rail, not security.

## Signing in

Login is real: `AuthService.signIn` posts email + password to `POST /api/auth/login`
and the backend checks them against the `users` collection in MongoDB Atlas. There's
no session token beyond that — a successful response is kept in a signal and mirrored
to `localStorage` so a refresh doesn't sign you out. Swapping in real identity
(Microsoft SSO) would only change `signIn`/`restore` in `auth.service.ts`; the rest of
the app just reads `user()`/`isAuthenticated()`.

There's one coordinator account, `reviewer@version1.com`, that lands on `/review`;
everyone else is an `employee` and lands on `/nominate`. Accounts are seeded into
MongoDB rather than hardcoded in the frontend — ask whoever owns the backend/seed data
for working credentials.

## The form

Four questions, matching the live Microsoft Form and the team's wireframe:

1. **Full name** of the colleague being nominated.
2. **Work email** — must match an existing Version 1 account. `UserDirectoryService`
   fetches the nominatable colleague list (`GET /api/users/nominatable`) once on load;
   typing an email that isn't in that list fails validation (`unknownNominee`), and a
   match shows a live preview of the person's name, department and location beneath
   the field. A `<datalist>` also offers native autocomplete against the same list.
   Can't be your own email either (`selfNomination`).
3. **Why**, split into **WHAT** (what they did and its impact) and **HOW** (which
   Core Values they demonstrated). 50–700 characters each, with a live counter.
4. **Category** — five cards with example tooltips, rather than a dropdown.

Plus an optional "send me an email receipt" tick box.

The nominator's name, email, practice and location are captured automatically from
the signed-in user — never typed. The case study requires this, and the dashboard
filters on practice and location.

One nomination per person per round: the confirmation screen offers no "submit
another" button.

## The reviewer dashboard

A table of every nomination (`GET /api/nominations`), filterable by status (Pending /
Accepted / Rejected / Shortlist / All), with a tab bar showing live counts. Per row:

- **Flags** — rule-based checks from the backend's `com.example.tagging` package
  (weak justification, routine-task language, reciprocal or repeat nominations),
  shown as coloured chips. `Review.flagClass()` keys off the tag name.
- **Claude review** — optional, one row at a time (`requestClaudeReview`) or in bulk
  across everything unreviewed (`runAllClaudeReviews`, sequential on purpose — each
  call is a live model request). Verdicts are cached by the backend, so re-requesting
  an already-reviewed nomination is free. Both flags and Claude's verdict are
  advisory; accept/reject is entirely the reviewer's call and can be taken back.
- **Accept / reject / shortlist (star)** — each write (`ReviewService`) returns the
  updated row, patched into the `rows` signal in place, so acting on one nomination
  doesn't refetch and recompute flags for all the others.

`NominationView` (`core/models/review.model.ts`) mirrors the backend's response
one-for-one, including joined-in `nominatorProfile`/`nomineeProfile` account context —
keep it in step when the Java side changes.

## Project layout

```
src/app/
  core/
    guards/auth.guard.ts           signed-out visitors → /login
    guards/role.guard.ts           reviewerGuard + nominatorGuard (role split)
    models/nomination.model.ts     categories, Core Values, quarter, API payload
    models/review.model.ts         NominationView, FlagResult, ClaudeReviewResult
    models/user.model.ts           AppUser
    services/auth.service.ts       real session (POST /api/auth/login), persisted to localStorage
    services/nomination.service.ts submits a nomination (mock or HTTP)
    services/review.service.ts     list/accept/reject/favourite/Claude-review calls
    services/user-directory.service.ts  nominatable colleague list for the nominee picker
  features/
    login/                         sign-in screen
    nominate/                      nomination form
    review/                        reviewer dashboard
src/environments/environment.ts    apiBaseUrl + useMockApi switch
src/styles.scss                    design tokens and shared form/button styles
public/star-award.png              the Star Award logo
```

Styling is hand-rolled SCSS, no UI library. Design tokens are CSS custom properties
at the top of `src/styles.scss` — navy `#052438` and teal `#00c6c2`, sampled from
the logo. Changing them there re-themes the whole app.

## Known gaps

- No session token: the login response is trusted as-is and mirrored to
  `localStorage`. Fine for this case study; a real deployment would put this form in
  Microsoft Teams and take identity from there instead.
- Collaboration & Engagement has placeholder example text (marked `TODO` in
  `nomination.model.ts`); the other four categories have the official wording.
- `CURRENT_QUARTER` in `core/models/nomination.model.ts` is set by hand — update it
  each nomination round.
- One-nomination-per-person is signposted in the UI but not enforced.
- The logo is the star glyph only, with no "STAR AWARD" wordmark, so the login
  screen shows no product name.
- `environment.useMockApi` (localStorage fallback in `NominationService`) still
  exists for demoing the form with the backend stopped, but stays `false` day to day
  — the API is live.
