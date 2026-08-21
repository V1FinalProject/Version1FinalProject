# VMOST — Star Awards Recognition Platform

*Version 2.2 · 20 August 2026 · Aligned to Requirements v3.0 following the requirement review*

## Vision

> **Recognition at Version 1 is effortless to give, visibly fair to receive, and runs as a programme owned by the organisation rather than by any one person.**

## Mission

> **We build and run the back office of the Star Awards programme. Employees nominate exactly as they do today; reviewers get everything they need to decide in one screen, a way to ask for more rather than reject, and a single action that carries a decision as far as the surrounding systems allow.**

## Objectives

| # | Objective | Baseline | Target |
| --- | --- | --- | --- |
| O1 | Reviewer handling time per nomination | Manual, ~6 steps | ≤ 2 min median |
|  | Human actions to issue a quarter's vouchers | One per award | One worklist pass or one export, flat as volume grows |
| O2 | Sustained throughput / deadline-week burst | ~300 per week at peak | 300/wk sustained; 3,000 in the deadline week |
|  | Deadline clustering | Treated as a risk | Treated as normal operation |
| O3 | Decisions within 5 working days | [baseline required] | ≥ 90% |
|  | Decisions and shortlists ready for the All Hands | Not consistent | ≤ 10 working days after the deadline |
| O4 | Trained active reviewers | 1, rising to 2 | ≥ 3, with zero single points of failure |
|  | New reviewer able to operate the platform unaided | Not achievable | Achieved through in-platform demo and documentation |
| O5 | Changes to the nominator's experience | n/a | Zero — the form is identical to today's |
|  | Correctable nominations resolved by clarification rather than rejection or deletion | Handled by email, or by deleting the record | Majority, with the record retained |
| O6 | Quarterly spreadsheet rebuilds | 1 per quarter | 0 — one live record per nomination |
|  | Records carrying practice and location from internal systems | Manual or absent | 100%, across the five divisions |
| O7 | Decisions made autonomously by AI | n/a | 0 — permanently |
|  | Reviewer context for relationship-based judgements | None | Org chart and distance shown on every review |
|  | Distribution and upward-nomination reporting | Not produced | Automated, every quarter |
| O8 | Participation growth vs added admin headcount | ~30% in 2 quarters | Growth with zero added headcount |
| O9 | Quarters paused or degraded during transition | n/a | 0 — cutover within one quarter boundary |
|  | Steps still done by email or Excel after cutover | All steps today | 0 within one quarter |

## Strategies

- **S1** — **Block what is certain, tag what needs judgement.** Self-nomination and blank fields are blocked — the system cannot be wrong about either. Reciprocity, upward nomination, routine language, weak justification and eligibility are tagged with their evidence and left to the reviewer. Nothing is ever decided automatically.
- **S2** — **Leave the employee's experience untouched.** The nomination form does not change — same fields, wording and layout. Quality is improved after submission through dialogue with the nominator, not before it through friction on the form.
- **S3** — **One record, one source of truth, cradle to grave.** A nomination is a single structured record enriched through review, issuance and payroll. Nothing is re-keyed, nothing is deleted, and no spreadsheet is rebuilt.
- **S4** — **Automate everything inside our reach, and hand off cleanly at the boundary.** The platform does not connect to Reachdesk. It produces the issuance worklist and the bulk-upload export, so the manual step is one action per run rather than one per award.
- **S5** — **Build for a team and a role, not for an owner.** Role-based access, a reviewer pool, an append-only audit trail, and a demo and documentation delivered as part of the product rather than after it.
- **S6** — **Programme rules are configuration, not code.** Deadline rule, quarter dates, payroll cut-offs, gift values, practices, tag types and email wording all editable by the programme owner.
- **S7** — **Transition without interruption.** Parallel run, then cutover at a quarter boundary. The programme does not pause, and the first release must be visibly simpler than what it replaces.

## Tactics

- **T1** — **Submission** — the existing form, replicated exactly. Self-nomination and blank fields blocked; nominator identity taken from sign-in; practice and location derived from internal systems rather than asked for. No evidence or proof fields, now or later.
- **T2** — **AI tagging** — reciprocity, upward nomination, routine-task language, weak justification and eligibility concern. Every tag shows the evidence that triggered it; a tag that cannot show its evidence is suppressed. All advisory, none blocking.
- **T3** — **Review** — one screen with nomination, tags, history and decision controls; queue filterable by practice, age and tag type including “no tags”; decided items greyed and labelled; collision protection; single-action approval; mandatory rejection reason feeding the nominator's email.
- **T4** — **Clarification** — instead of deciding, a reviewer can ask the nominator for more. The nominator gets a link to a side-by-side view: their original nomination static with the request over it, an editable copy alongside. Both versions retained.
- **T5** — **Relationship context** — local org charts for nominator and nominee, and the distance between them, so the reviewer can judge upward and reciprocal nominations on evidence rather than impression.
- **T6** — **Issuance and payroll** — a voucher worklist of approved awards with name, email, location and value from internal systems and the Guidelines table; a Reachdesk-shaped export; awards never aggregated; payroll notified from the same data before the cut-off.
- **T7** — **Communications and record** — both parties notified on approval with the nomination content; rejection feedback and resubmission reminder from the review decision; every email logged against the nomination; wording editable without a release.
- **T8** — **Dashboard, access and audit** — live whole-quarter view with practice segmentation and export; role-based access including Comms and Engagement and division leadership; an append-only audit trail in which nothing is ever deleted.
- **T9** — **Adoption and transition** — an in-platform demo and written guidance a first-time reviewer can use unaided; a runbook covering the quarter cycle and deadlines; parallel run then cutover at a quarter boundary; a leadership engagement plan for All-Star selection.
