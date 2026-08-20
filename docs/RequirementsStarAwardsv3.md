# Requirements — Star Awards Recognition Platform

*Version 3.0 · 20 August 2026 · MVP scope, Star Award tier · Reviewed and prioritised with the programme team*

"The system shall" is implied in every requirement. **Must** = go-live blocked without it. **Should** = significant value, omission is an accepted degradation for one release. **Could** = first to be dropped under pressure. **N/A** = retired in place, ID kept so existing references still resolve.


## Data

| ID | Priority | Requirement | Acceptance criteria |
| --- | --- | --- | --- |
| DAT-01 | Must | Hold one record per nomination carrying ID, parties, practice, location, category, dates, text, tags, status, reviewer, reason, comms, fulfilment batch and payroll reference. | Record count equals submissions received, with no orphans or duplicates at quarter close; every later requirement reads or writes this record. |

## Submission

| ID | Priority | Requirement | Acceptance criteria |
| --- | --- | --- | --- |
| SUB-01 | Must | The nomination form shall replicate the existing form exactly — same fields, wording, order and layout. No fields added, removed, reworded or reordered. | Every field matches the current form in label, help text, type, order and mandatory status; no evidence, proof or additional justification field is added. |
| SUB-02 | Must | Block submission where nominator and nominee are the same person. | Self-selection is refused with a clear message; the nominator can immediately select a different nominee without losing entered text. |
| SUB-03 | Must | Capture nominator identity from the authenticated session; it cannot be entered or altered. | Identity populates read-only from internal systems; unauthenticated requests are refused. Precondition for SUB-02 and ELG-02. |
| SUB-04 | Must | Derive practice and location for nominator and nominee from internal systems (BambooHR / employee directory) rather than capturing them on the form. | Populated without nominator entry; the form is unchanged; practice resolves to one of the five divisions; lookup failure flags the nomination rather than refusing it. |
| SUB-05 | Could | Show remaining quarterly entitlement and the deadline date, and allow drafts to be saved and resumed. | Entitlement and deadline shown before submission; a saved draft restores all content and does not consume entitlement. Note: alters the form — resolve against SUB-01. |

## Eligibility and entitlement

| ID | Priority | Requirement | Acceptance criteria |
| --- | --- | --- | --- |
| ELG-01 | Must | Verify nominator and nominee are active full- or part-time employees; contractors are ineligible. | Contractor nominator refused; contractor nominee accepted but tagged; directory outage accepts and flags the submission rather than refusing it. |
| ELG-02 | Must | Permit one active or completed nomination per eligible employee per quarter. | A second submission is refused while one is pending or approved, and the existing nomination is identified. |
| ELG-03 | Must | Apply the deadline of the last Friday of the quarter; entitlement lapses and never carries over, including across leave. | Submissions after the deadline attribute to the next quarter; unused entitlement lapses equally for all employees. Open: does entitlement lapse on the Friday or at quarter end? |
| ELG-04 | Must | Restore entitlement when a nomination is rejected, allowing one replacement in the same quarter. | Replacement is accepted and linked to the original ID; the original moves to Superseded and is never deleted. |

## AI tagging

| ID | Priority | Requirement | Acceptance criteria |
| --- | --- | --- | --- |
| TAG-01 | Must | Apply advisory tags to every submission. Tags never approve, reject, block or withdraw. | A nomination carrying every possible tag remains approvable in one action; tagging outage queues the nomination untagged and marked as such. |
| TAG-02 | Must | Support tags for reciprocity, upward nomination of a line manager, routine-task language, weak justification and eligibility concern. | Each condition raises its tag; disabling a tag type in configuration stops it being applied, with no code release. |
| TAG-03 | Must | Present the triggering evidence with every tag. | Text tags highlight the triggering phrase; pattern tags link the related record; eligibility tags show the attribute and value; a tag without producible evidence is suppressed. |

## Review

| ID | Priority | Requirement | Acceptance criteria |
| --- | --- | --- | --- |
| REV-01 | Must | Present nomination, tags with evidence, nominee and nominator history, and decision controls on one screen. | All elements visible without navigating away; absence of tags or history stated explicitly. History is context, not a warning — repeat recognition is permitted. |
| REV-02 | Must | Provide a queue filterable and sortable by practice, location, category, age and tag type, where tag type includes an explicit “no tags” option. | Only nominations awaiting decision listed by default; filters persist for the session; “no tags” returns unflagged nominations only. |
| REV-03 | Must | Prevent two reviewers deciding the same nomination, and show decision state clearly in the dashboard. | Awaiting review shows a Review button; decided nominations are greyed out with an Approved or Rejected label and cannot be reopened; a nomination open with one reviewer is unavailable to another and returns to the queue on exit or timeout. |
| REV-04 | Must | Record a decision and trigger every reachable downstream process from a single confirmed approval: record update, both notifications, reporting, and addition to the voucher worklist. The platform does not connect to Reachdesk. | Decision, reviewer and timestamp recorded; all downstream steps initiated with no further reviewer action. |
| REV-05 | Must | Require a structured reason and free-text feedback on every rejection, and email both to the nominator with a reminder that they may submit a new nomination before the end of the quarter. | Rejection without either is prevented; the email states the deadline as a specific date, and says plainly where insufficient time remains to resubmit. |
| REV-06 | Should | Allow a reviewer to request a clarification or update from the nominator instead of deciding. The nominator receives a link to a side-by-side view: their original nomination static with the reviewer's request over it, and an editable copy alongside. | Original text, request, revision and timestamps all retained; resubmission returns it to the queue; does not consume entitlement. Open: response window, and whether it pauses the decision clock. |
| REV-07 | Should | Display the local org chart for the nominator and for the nominee, and the distance between them in the org chart. | A local chart is shown for each party; the distance between them is shown; reporting data comes from internal systems; missing data is stated rather than shown as an empty chart. |

## Fulfilment

| ID | Priority | Requirement | Acceptance criteria |
| --- | --- | --- | --- |
| FUL-01 | Must | Maintain a voucher issuance worklist of approved awards, with an export formatted for Reachdesk's bulk upload. The platform does not connect to Reachdesk. | Worklist shows name, email, location and value; location from internal systems; value from the Guidelines location and tier table; awards never aggregated; issuance markable individually or by batch; records show the value actually issued at the time. |
| FUL-02 | N/A | Superseded — merged into FUL-01. | Award value resolution and the no-aggregation rule are handled by FUL-01. |
| FUL-03 | Must | Notify payroll of every fulfilled award before the applicable cut-off, drawing on the same worklist data as FUL-01. | Awards fulfilled before the cut-off appear in that period's notification; later awards carry to the next period and are identified as such. Open: cut-off dates, format and transport. |

## Communications

| ID | Priority | Requirement | Acceptance criteria |
| --- | --- | --- | --- |
| COM-01 | Must | On approval, notify both nominator and nominee that the nomination has been accepted. Both receive the details of the approved submission. | Both emails contain both names and the nomination content as submitted; send timestamps recorded against the record. |
| COM-02 | N/A | Superseded — no separate rejection communication is required. | Rejection feedback and the resubmission reminder are handled by REV-05. |
| COM-03 | Should | Keep a record of every email the platform sends about a nomination. | Each email recorded against its nomination with type, recipient, date and delivery outcome; failures visible rather than silent; opening a nomination shows its full email history in order. |
| COM-04 | Could | Allow the wording of the platform's emails to be changed without a code release. | Wording held in editable settings; an edit applies to the next email of that type and records who changed it and when. |

## Dashboard

| ID | Priority | Requirement | Acceptance criteria |
| --- | --- | --- | --- |
| DSH-01 | Should | Provide a live whole-quarter status view with practice segmentation and export, requiring no manual rebuild. | Shares filtering with REV-02 and decision-state display with REV-03 — one capability presented in two places. Export contains exactly the filtered set. |

## Access and audit

| ID | Priority | Requirement | Acceptance criteria |
| --- | --- | --- | --- |
| SEC-01 | Must | Enforce role-based access so each role can see and do only what its role requires. | Roles: nominator, reviewer, programme owner, Comms and Engagement, division leadership. A nominator sees only their own submissions; a rejected nomination is never visible to the person it named; tags and reviewer notes never visible to nominators or nominees. |
| SEC-02 | Must | Keep an append-only audit trail of every decision, amendment and configuration change. | Each entry records actor, action, target and time; configuration changes record before and after; no role can edit or delete an entry; nominations are never deleted; a quarter can be reconstructed end to end. |

## Adoption and handover

| ID | Priority | Requirement | Acceptance criteria |
| --- | --- | --- | --- |
| ADO-01 | Must | Provide a platform demo and written guidance for new reviewers, covering how to review a nomination, what each part of the screen means, the systems the platform draws on, and the quarter cycle including the deadline and payroll dates. | A guided walkthrough is available inside the platform to a first-time reviewer without anyone setting it up for them; written documentation is maintained alongside the platform and updated in the same release as any change affecting reviewers; a reviewer can reach the relevant guidance from the review screen without leaving it. Delivers NFR-03. |

## Non-functional

| ID | Priority | Requirement | Acceptance criteria |
| --- | --- | --- | --- |
| NFR-01 | Must | Sustain 300 submissions per week and absorb 3,000 in the deadline week, including 1,000 in any hour. | No data loss or submission errors under burst; review screen loads within 2s and search returns within 3s at the 95th percentile. |
| NFR-02 | Should | Achieve 99.5% availability in the two weeks to the deadline and degrade gracefully on dependency failure. | Directory, email or tagging outage never prevents submission, review or decision recording; maintenance never falls in the final two weeks. |
| NFR-03 | Must | Enable a new reviewer with no programme experience to effectively use the platform. | Achieved using only in-platform guidance, with no access to an experienced colleague. Note: no longer time-bound — consider “reach a defensible first decision” to keep it testable. |
