# Star Awards — UAT Checklist

User Acceptance Testing: working through the **live, hosted** site as a real
nominator and a real coordinator would, to confirm the app actually does the
job — not a code review, not hunting for edge cases, just "does this work the
way someone using it for real would expect."

**Tester: Akos Bujdoso**
**Date: 24/08/2026**
**URL tested: rcatcher-5brj.onrender.com**
**Browser/device: Microsoft Edge, Windows 11 Enterprise x86**

For each row: do the steps, compare against Expected, mark Pass/Fail, jot a
note if anything felt off (even if it technically passed).

## Login

| # | Steps | Expected | Result | Notes |
| --- | --- | --- |--------| --- |
| 1 | Go to the site signed out | Redirected to `/login` | Pass   | |
| 2 | Log in as an employee account | Lands on the nomination form | Pass   | |
| 3 | Log in as the coordinator account | Lands on the review dashboard | Pass   | |
| 4 | Log in with a wrong password | Clear error, not let in | Pass   | |
| 5 | Log in with an email that doesn't exist | Clear error, not let in | Pass   | |
| 6 | Refresh the page while signed in | Still signed in (no re-login needed) | Pass   | |
| 7 | Sign out | Returned to login, can't reach `/nominate` or `/review` after | Pass   | |

## Nomination form (as an employee)

| # | Steps | Expected | Result | Notes                                                                                                                                                                                                                                 |
| --- | --- | --- |--------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 8 | Start typing a colleague's email in the nominee field | Live preview shows their name/department once it matches | Pass   | Does not auto fill full name based on email, not bug, only potential feature                                                                                                                                                          |
| 9 | Enter an email that isn't a real Version 1 account | Validation error, can't submit | Pass   |                                                                                                                                                                                                                                       |
| 10 | Try to nominate yourself | Blocked with a clear message | Pass   |                                                                                                                                                                                                                                       |
| 11 | Leave WHAT or HOW under the minimum length | Blocked, counter shows red/warning | Pass   |                                                                                                                                                                                                                                       |
| 12 | Fill WHAT/HOW past the max length | Blocked, counter shows over-limit | Pass   |                                                                                                                                                                                                                                       |
| 13 | Pick a category and check the tooltip/examples | Examples shown match the category | Pass   | Examples tooltip not shown on smaller displays, fine on large screens                                                                                                                                                                 |
| 14 | Fill the form out properly and submit | Confirmation screen with a reference number | Pass   | Server slow to process form and display confirmation                                                                                                                                                                                  |
| 15 | After submitting, try to submit another nomination | No "submit another" option offered | Pass   |                                                                                                                                                                                                                                       |
| 16 | Already submitted this round — go straight back to `/nominate` (not through the confirmation button) and submit again for the same colleague | Should be blocked or warned: one nomination per person per round | Fail   | Known gap, not a bug found today — only signposted (confirmation screen hides the resubmit button); nothing actually stops a fresh visit to `/nominate` from submitting a duplicate. Documented in `frontend/README.md`'s Known gaps. |

## Review dashboard (as the coordinator)

| # | Steps | Expected | Result | Notes                                                                                                                        |
| --- | --- | --- |--------|------------------------------------------------------------------------------------------------------------------------------|
| 17 | Open the dashboard | List of nominations loads, Pending tab selected by default | Pass   | Due to free tier backend/database hosting, serving is slow                                                                   |
| 18 | Switch between Pending / Accepted / Rejected / Shortlist / All tabs | Each shows the right subset, counts match | Pass   | Counts correct                                                                                                               |
| 19 | Expand a nomination row | Nominator/nominee detail (job title, department, etc.) shown | Pass   | Dropdown information hard to digest, future expansion perhapsa necessary                                                     |
| 20 | Accept a nomination | Moves to Accepted, decision can be undone | Pass   |                                                                                                                              |
| 21 | Reject a nomination | Moves to Rejected, decision can be undone | Pass   |                                                                                                                              |
| 22 | Star a nomination | Appears under Shortlist | Pass   |                                                                                                                              |
| 23 | Run a Claude review on one nomination | Verdict appears, doesn't block the rest of the page | Pass   |                                                                                                                              |
| 24 | Run "Review all pending" | Works through the queue, shows progress, doesn't stall on one failure | Fail   | Claude API account ran out of tokens during testing. Considered dropping for final version. (Not necessary for requirements) |
| 25 | Check a nomination with a rule-based flag (e.g. weak justification) | Flag chip shown with reasoning | Pass   |                                                                                                                              |

## Access control

| # | Steps | Expected | Result | Notes |
| --- | --- | --- |--------| --- |
| 26 | As an employee, try to visit `/review` directly | Bounced to `/nominate`, not shown the dashboard | Pass   | |
| 27 | As the coordinator, try to visit `/nominate` directly | Bounced to `/review`, not shown the form | Pass   | |

## Sign-off

**Overall result:** Pass with issues
**Issues found:** From can be submitted more than one time per quarter.


**Ready for presentation?** Y
