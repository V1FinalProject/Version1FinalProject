# Initial Review — Implementation vs. Spec

*20 August 2026 · Reviewed against `RequirementsStarAwardsv3.md` (primary spec), the VMOST, user stories, guidelines, and FAQ · Angular frontend + Java/Spring backend*

This is a point-in-time check of the current solution against the stakeholder spec. It only calls out specific, key deviations — not a full line-by-line audit, and not a judgement on code quality. Requirement IDs referenced are from `RequirementsStarAwardsv3.md`.

## Must-priority gaps

### 1. Rejection has no reason or feedback (REV-05)
Spec: rejection requires a mandatory structured reason *and* free-text feedback, both emailed to the nominator along with the resubmission deadline.

Implementation: the decision API takes only a status enum, and the UI's reject button calls it directly with nothing else.
- `backend/src/main/java/com/example/tagging/ReviewDecisionRequest.java:4` — `record ReviewDecisionRequest(ReviewStatus status)`, no reason/feedback fields.
- `frontend/src/app/features/review/review.ts:142-146` — `decide()` just flips `PENDING`/`ACCEPTED`/`REJECTED`, no reason capture.

### 2. No communications are sent at all (COM-01, REV-05, COM-03)
Spec: approval must email both nominee and nominator with the nomination content; rejection must email the reason and feedback; every email is logged against the nomination.

Implementation: there is no email/notification capability anywhere in the codebase — no mail dependency in `backend/pom.xml`, no mail service class, and `decide()` in `NominationReviewService` only updates status. Approving or rejecting a nomination today has zero downstream communication effect.

### 3. No server-side authorization — access control is frontend-only (SEC-01)
Spec: role-based access enforced so nominators, reviewers, programme owner, Comms and Engagement, and division leadership each see and do only what their role permits; a nominator never sees a rejected nomination naming them; tags/notes are hidden from nominators.

Implementation: the backend has no security dependency (`spring-boot-starter-security` etc. absent from `pom.xml`) and none of `NominationController`/`NominationReviewController` check an authenticated caller or role. All access control is a client-side route guard (`frontend/src/app/core/guards/role.guard.ts`) built on a mock, localStorage-backed session (`frontend/src/app/core/services/auth.service.ts`). Anyone can call `GET/PUT /api/nominations/**` directly and read or decide every nomination, bypassing the UI entirely. Only two roles exist (`employee`, `coordinator`) versus the five specified.

### 4. No audit trail (SEC-02)
Spec: an append-only log of every decision, amendment and configuration change, reconstructable end to end.

Implementation: no audit log entity, collection, or write path exists anywhere in `backend/src/main/java/com/example/tagging`.

### 5. Quarterly entitlement is not enforced, and rejection doesn't restore/link it (ELG-02, ELG-04)
Spec: one active/completed nomination per employee per quarter; a rejected nomination restores entitlement for exactly one linked replacement, and the original is marked Superseded (never deleted).

Implementation:
- `NominationSubmissionRequest.validate()` (`backend/src/main/java/com/example/tagging/NominationSubmissionRequest.java:43-59`) never checks for an existing pending/approved nomination from the same nominator this quarter — a nominator can submit unlimited nominations.
- `ReviewStatus` (`backend/src/main/java/com/example/tagging/ReviewStatus.java`) has only `PENDING/ACCEPTED/REJECTED` — no `SUPERSEDED` state, and no field anywhere linking a resubmission back to the original nomination's ID.

### 6. Contractor eligibility is a stub (ELG-01)
Spec: contractors must be blocked from nominating and tagged if nominated.

Implementation: `NotEmployeeStatusChecker` (`backend/src/main/java/com/example/tagging/NotEmployeeStatusChecker.java`) is an explicit no-op — its own comment says "always passes (never flags)" pending a real employee-data source. No contractor check exists on the submission path either.

### 7. Voucher worklist and payroll notification don't exist (FUL-01, FUL-03)
Spec: an issuance worklist of approved awards with a Reachdesk-shaped bulk export (name, email, location, value from the Guidelines table, never aggregated), plus a payroll notification drawn from the same data before each cut-off.

Implementation: no worklist, export, or payroll-notification code exists. Approving a nomination has no effect beyond flipping its status — there's nothing downstream to award-issue from. This is also blocked by finding 8 below, since the data needed to price an award (nominee's location) isn't captured.

## Should/data-model gaps worth flagging early

### 8. Only the nominator's practice/location is captured — not the nominee's (DAT-01, SUB-04)
Spec: derive practice and location "for nominator **and nominee**" from internal systems; the dashboard and voucher worklist both need the nominee's location (to price the award from the Guidelines table) and practice (for segmentation).

Implementation: `Nomination` (`backend/src/main/java/com/example/tagging/Nomination.java:26-38`) has a single `practice`/`location` pair — populated from the signed-in nominator only (`frontend/src/app/features/nominate/nominate.ts:158-163`). There's no field for the nominee's practice/location anywhere in the model. Separately, `practice`/`location` are sourced from a hardcoded `DEMO_USERS` array (`frontend/src/app/core/models/user.model.ts:28-72`) rather than an internal-systems lookup, which is an acceptable stand-in for now but means the "lookup failure flags rather than refuses" acceptance criterion has nothing to test against yet.

### 9. Review queue can't be filtered by practice, location, category, age or tag type (REV-02)
Implementation: `frontend/src/app/features/review/review.ts:16-22` filters only by decision status (Pending/Accepted/Rejected/Shortlist/All) — no practice, location, category, age, or tag-type filters, and no explicit "no tags" option.

### 10. No relationship context or upward-nomination tag (REV-07, TAG-02)
Spec: local org charts for nominator and nominee with the distance between them, and an advisory tag for upward nomination of a line manager.

Implementation: the registered flag checkers cover Reciprocal, Repeat, Routine Language, Weak Justification, and the stubbed employee-status check — there is no upward-nomination checker and no org-chart/relationship data anywhere in the backend or frontend.

### 11. No reviewer collision protection (REV-03)
Spec: a nomination open with one reviewer must be unavailable to another, and return to the queue on exit/timeout.

Implementation: no locking or claim mechanism exists — `ReviewStateStore`'s own doc comment notes "there is only one reviewer account today," and decisions aren't keyed by reviewer at all, so this can't yet be tested or relied on once a second reviewer is added.

## Not reviewed here
Clarification workflow (REV-06), comms wording configurability (COM-04), in-platform demo/guidance (ADO-01), and the non-functional/throughput requirements (NFR-01–03) are lower-priority "Should"/"Could" items or require load testing to assess, and are left out of this pass to keep it focused on the Must-priority gaps above.
