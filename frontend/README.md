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
Everything works through the built-in terminal:

1. **File → Open** and select the `frontend` folder.
2. Mark `node_modules`, `dist` and `.angular` as excluded if IDEA hasn't already:
   right-click → **Mark Directory as → Excluded**. Keeps indexing fast.
3. Open the terminal (`Alt+F12`), run `npm install` (first time) then `npm start`.
4. Open <http://localhost:4200/>. Saving a file hot-reloads the browser.

For a one-click run button, add a **Shell Script** run configuration with *Script
text* `npm start` — those are available in CE.

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

`AppUser.role` is either `employee` or `coordinator`, and the two are mutually
exclusive: employees nominate and can't reach the dashboard; coordinators review and
can't nominate, since a coordinator who nominated would review their own submission.
Enforced by `core/guards/role.guard.ts` — a UX guard rail, not security.

## Demo accounts

Invented people, shaped like real records so the same rows can be seeded into MySQL.
Clicking one on the login screen fills in the credentials; any non-empty password is
accepted. They live in `core/models/user.model.ts`.

| Name | Email | Practice | Location | Role |
| --- | --- | --- | --- | --- |
| Joe Duffy | joe.duffy@version1.com | Digital, Data & Cloud | Dublin | employee |
| Niamh Gallagher | niamh.gallagher@version1.com | Enterprise Applications | Belfast | employee |
| Rory McKenna | rory.mckenna@version1.com | Managed Services | Birmingham | employee |
| Aoife Byrne | aoife.byrne@version1.com | Consulting & Advisory | Cork | employee |
| Star Award Reviewer | **reviewer@version1.com** | People & Culture | Dublin | **coordinator** |

## The form

Four questions, matching the live Microsoft Form and the team's wireframe:

1. **Full name** of the colleague being nominated.
2. **Work email** — must be `@version1.com`, and can't be your own.
3. **Why**, split into **WHAT** (what they did and its impact) and **HOW** (which
   Core Values they demonstrated). 50–700 characters each, with a live counter.
4. **Category** — five cards with example tooltips, rather than a dropdown.

Plus an optional "send me an email receipt" tick box.

The nominator's name, email, practice and location are captured automatically from
the signed-in user — never typed. The case study requires this, and the dashboard
filters on practice and location.

One nomination per person per round: the confirmation screen offers no "submit
another" button.

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

Styling is hand-rolled SCSS, no UI library. Design tokens are CSS custom properties
at the top of `src/styles.scss` — navy `#052438` and teal `#00c6c2`, sampled from
the logo. Changing them there re-themes the whole app.

## Known gaps

- Submissions go to browser `localStorage`, not a server, while
  `environment.useMockApi` is `true`.
- Authentication is a stub. In a real deployment this form would live in Microsoft
  Teams and identity would come from there.
- The `/review` dashboard is a scaffold: stat tiles and a placeholder where the
  swipe card stack will go.
- Collaboration & Engagement has placeholder example text (marked `TODO` in
  `nomination.model.ts`); the other four categories have the official wording.
- `CURRENT_QUARTER` in `core/models/nomination.model.ts` is set by hand — update it
  each nomination round.
- One-nomination-per-person is signposted in the UI but not enforced.
- The logo is the star glyph only, with no "STAR AWARD" wordmark, so the login
  screen shows no product name.
