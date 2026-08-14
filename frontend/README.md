# Star Awards — Frontend

Angular 22 frontend for the Star Awards recognition platform. Deployment target is
Azure (static hosting), talking to the Spring Boot API with Azure Database for MySQL
behind it.

Colleagues submit nominations through a guided form; a coordinator reviews them on a
swipe-style dashboard with ML flags alongside. The form is built; the dashboard is
currently a scaffold.

## Running in IntelliJ IDEA Community Edition

IDEA CE doesn't bundle the JavaScript/TypeScript or Node.js plugins (those are
Ultimate-only), so there's no Angular tool window and no npm run configurations.
Everything works fine through the built-in terminal:

1. **File → Open** and select the `frontend` folder (open it as its own project —
   opening the repo root works too, but indexing is slower).
2. Mark `node_modules`, `dist` and `.angular` as excluded if IDEA hasn't already:
   right-click the folder → **Mark Directory as → Excluded**. This keeps indexing fast.
3. Open the terminal (`Alt+F12`) and run:

   ```bash
   npm install   # first time only
   npm start
   ```

4. Open <http://localhost:4200/>. Saving a file hot-reloads the browser.

If you want a one-click run button, add a **Shell Script** run configuration
(Run → Edit Configurations → + → Shell Script), set *Script text* to `npm start`
and *Working directory* to this folder. Shell Script configs are available in CE.

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
| `/login` | Dummy sign-in with a demo account picker | Anyone |
| `/nominate` | The nomination form | Signed-in employees |
| `/review` | Reviewer dashboard (scaffold) | Coordinators only |
| `/` | Redirects by role — coordinators to `/review`, everyone else to `/nominate` | — |

### Roles

`AppUser.role` is either `employee` or `coordinator`, and the two are mutually
exclusive by design:

- **Employees** nominate. They can't reach the dashboard.
- **Coordinators** review. They can't submit nominations — `reviewer@version1.com`
  is a shared role account rather than a person, and a coordinator who nominated
  would end up reviewing their own submission.

Both rules are enforced by `core/guards/role.guard.ts`. Like all client-side
checks, they're a UX guard rail, not security — the API must enforce the same
rules itself.

## Demo accounts

Invented people, shaped like real records so the same rows can be seeded into
MySQL. Clicking one on the login screen fills in the credentials for you; any
non-empty password is accepted.

| Name | Email | Practice | Location | Role |
| --- | --- | --- | --- | --- |
| Joe Duffy | joe.duffy@version1.com | Digital, Data & Cloud | Dublin | employee |
| Niamh Gallagher | niamh.gallagher@version1.com | Enterprise Applications | Belfast | employee |
| Rory McKenna | rory.mckenna@version1.com | Managed Services | Birmingham | employee |
| Aoife Byrne | aoife.byrne@version1.com | Consulting & Advisory | Cork | employee |
| Star Award Reviewer | **reviewer@version1.com** | People & Culture | Dublin | **coordinator** |

They live in `core/models/user.model.ts`.

## The form

Four questions, matching the live Microsoft Form and the team's wireframe:

1. **Full name** of the colleague being nominated.
2. **Work email** — must be `@version1.com`, and can't be your own (self-nomination
   is blocked, per the case study).
3. **Why**, split into **WHAT** (what they did and its impact) and **HOW** (which
   Core Values they demonstrated). 50–700 characters each, with a live counter.
4. **Category** — five cards with example tooltips, rather than a dropdown.

Plus an optional "send me an email receipt" tick box.

The nominator's name, email, practice and location are captured automatically from
the signed-in user and shown in a panel above the submit button — never typed. The
case study requires this, and the dashboard filters on practice and location.

One nomination per person per round: the confirmation screen offers no "submit
another" button. Note this isn't *enforced* yet — nothing stops a determined user
navigating back to `/nominate`. Real enforcement needs a "has this nominator
already submitted this quarter?" check against the database.

## Where submitted data goes right now

Nowhere off your machine. Until the API exists, submissions are written to browser
**localStorage** under two keys:

| Key | Contents |
| --- | --- |
| `star-awards.nominations` | Array of every nomination submitted in this browser |
| `star-awards.session` | The signed-in demo user |

To inspect them, open DevTools (`F12`) → **Application** → **Local Storage** →
`http://localhost:4200`, or run in the console:

```js
JSON.parse(localStorage.getItem('star-awards.nominations'))
```

Consequences worth knowing while demoing:

- Data is per-browser and per-profile. Clearing site data wipes it; a different
  browser sees nothing.
- The `SA-2026-0003` reference is just `SA-{year}-{array length}`, so two people
  would both get `SA-2026-0001`. The server has to own reference generation.
- The dashboard's "Awaiting review" count reads this same array — it is not live
  API data, and the page says so.

## Project layout

```
src/app/
  core/
    guards/auth.guard.ts           signed-out visitors → /login
    guards/role.guard.ts           reviewerGuard + nominatorGuard (role split)
    models/nomination.model.ts     categories, Core Values, quarter, API payload
    models/user.model.ts           AppUser + the demo accounts
    services/auth.service.ts       fake session, persisted to localStorage
    services/nomination.service.ts submits a nomination (mock or HTTP)
  features/
    login/                         sign-in screen
    nominate/                      nomination form
    review/                        reviewer dashboard (scaffold)
src/environments/environment.ts    apiBaseUrl + useMockApi switch
src/styles.scss                    design tokens and shared form/button styles
public/star-award.png              the Star Award logo
```

### Styling

Hand-rolled SCSS, no UI library. Design tokens are CSS custom properties at the top
of `src/styles.scss` — brand colours are sampled from the logo and the existing
Microsoft Forms header: navy `#052438`, teal `#00c6c2`. Changing them there
re-themes the whole app.

## Notes for the backend team

Nominations are currently written to `localStorage` rather than POSTed, because the
API doesn't exist yet. To switch over, set `useMockApi: false` in
`src/environments/environment.ts` and point `apiBaseUrl` at the API — nothing else
changes.

The expected endpoint is `POST {apiBaseUrl}/nominations`, taking
`NominationSubmission` (see `src/app/core/models/nomination.model.ts`):

```jsonc
{
  "nomineeName":    "Jordan Whelan",
  "nomineeEmail":   "jordan.whelan@version1.com",
  "what":           "…what they did and its impact, max 700 characters…",
  "how":            "…which Core Values they demonstrated, max 700 characters…",
  "categoryId":     "customer-impact",
  "emailReceipt":   true,

  // captured automatically from the signed-in nominator, never typed
  "nominatorId":    "u-001",
  "nominatorName":  "Joe Duffy",
  "nominatorEmail": "joe.duffy@version1.com",
  "practice":       "Digital, Data & Cloud",
  "location":       "Dublin",

  "quarter":        "Q4 2026",
  "submittedAt":    "2026-08-14T09:00:00.000Z"
}
```

It should respond with `{ "reference": "SA-2026-0042", "submittedAt": "…" }`.

`what` and `how` are the two halves of "why are you nominating this colleague",
matching the WHAT/HOW split in the case study and the team's wireframe. `quarter`
is included because the AI tagging layer needs it to detect repeat nominations in
consecutive quarters — it's a hand-set constant (`CURRENT_QUARTER`), because the
programme's quarters don't follow calendar quarters.

The five `categoryId` values are stable kebab-case keys, suitable for a lookup
table or enum:

```
collaboration-and-engagement
customer-impact
innovation-and-growth
performance-and-efficiency
quality-and-compliance
```

### Things to get right on your side

- **Re-validate everything.** Self-nomination blocking, the `@version1.com` domain
  check, the character limits and the role split are all client-side only. A
  browser can bypass every one of them.
- **Don't trust the nominator block.** `nominatorId`, `practice` and `location` are
  currently sent by the browser. Once real authentication exists, read them from
  the token or the `users` record instead — otherwise anyone can submit a
  nomination as someone else.
- **Generate the reference server-side.** The frontend's `SA-YYYY-NNNN` is a local
  counter and will collide across users.
- **The payload is only the submission half of the record.** The dashboard also
  needs review status, rejection reason, decision date, comms sent date, and a link
  from a resubmission back to the original nomination ID. Those are written during
  review — worth designing into the table from the start.
- **Decide how Angular reaches the API on Azure.** Static Web Apps with a linked
  backend avoids CORS entirely; App Service means configuring CORS explicitly.

## Known gaps

- Authentication is a stub. In a real deployment this form would live in Microsoft
  Teams and identity would come from there — the dummy login exists only so the
  nominator's name, practice and location can be captured automatically.
- The `/review` dashboard is a scaffold: stat tiles and a placeholder where the
  swipe card stack will go. It needs the backend to serve nominations and ML flags.
- Collaboration & Engagement has placeholder example text (marked `TODO` in
  `nomination.model.ts`); the other four categories have the official wording.
- `CURRENT_QUARTER` in `core/models/nomination.model.ts` is set by hand — update it
  each nomination round.
- One-nomination-per-person is signposted in the UI but not enforced.
- The logo is the star glyph only, with no "STAR AWARD" wordmark, so the login
  screen shows no product name.
