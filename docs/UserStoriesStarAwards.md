# User Stories — Star Awards Recognition Platform

*Version 1.0 · 20 August 2026 · Aligned to Requirements v3.0*

Format: **As a** \<role\> **I can** \<capability\> **so that** \<benefit\>.

---

## Reviewer

### US-01 — Work the queue and approve

**As a** Star Awards reviewer, **I can** work through nominations filtered by oldest unreviewed first, and approve each in a single action that automatically handles everything downstream except issuing the voucher through Reachdesk, **so that** I only spend time on the decision itself.

*Traces to:* REV-02, REV-03, REV-04, FUL-01, COM-01

### US-02 — Start reviewing without prior experience

**As a** reviewer new to the Star Awards programme, **I can** find my way around the platform without being shown how, with a demo and guidance available on the platform itself, **so that** I can start reviewing nominations confidently without needing an experienced colleague to explain it to me.

*Traces to:* ADO-01, NFR-03, REV-01

### US-03 — Reject a nomination

**As a** Star Awards reviewer, **I can** reject a nomination by recording a reason and a short note, which automatically emails the nominator to tell them their nomination was not successful and that they can submit another Star nomination before the end of the quarter, **so that** the nominator is told promptly and given a fair chance to try again, without me writing the email myself.

*Traces to:* REV-05, ELG-04, COM-03

### US-04 — Use AI tagging as an aid

**As a** Star Awards reviewer, **I can** see AI tags on a nomination while I review it, each showing the evidence that triggered it, and toggle those tags on or off as I work, **so that** I can use them to reach a decision faster when they help and read the nomination on its own merits when they don't.

*Traces to:* TAG-01, TAG-02, TAG-03, REV-01, REV-02

> **Gap:** the show/hide toggle is not currently specified in Requirements v3.0 — TAG-01 to TAG-03 cover the tags themselves but not a reviewer control to hide them. Needs adding to REV-01 or as its own requirement.
