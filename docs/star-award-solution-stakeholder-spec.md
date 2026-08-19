Case Study: Star Awards 

Executive Summary 

This project proposes a dedicated recognition platform, augmented with AI, to replace the manual workflow end-to-end. Employees submit nominations through a guided interface that improves quality at source, coordinators review pre-validated entries, communications to nominators and nominees are generated and sent automatically, and a live dashboard replaces the quarterly rebuild of tracking spreadsheets. 

Problem Statement 

Star Awards is a strategic recognition programme designed to reinforce our values, celebrate impact, and build a sense of belonging across Version 1. However, the current process is manual and relies on Microsoft Forms, Outlook, and Excel. This is particularly challenging at the end of each quarter, when nominations peak (around 300 per week), requiring a significant administrative effort to manage the reviews and communications needed to run the programme effectively. 

User Flow 

-    Submission: The nominator completes the form, providing: 
    

-    WHAT: A clear description of the achievement, contribution, or action. 
    

-    HOW: An explanation of how the individual demonstrated Version 1’s core values. 
    

-    The completed form is submitted. 
    

-    AI Tagging: AI reads each submission and validates against programme rules (routine-task language, insufficient justification, repeat nomination in consecutive quarters, reciprocal nomination).  
    

-    Review: The nomination is reviewed for: 
    

-    Completeness of information. 
    

-    Alignment with program guidelines and core values. 
    

The coordinator decides: 

-    If Approved: Proceeds to recognition. Issue gift card on Reachdesk (external platform). 
    

-    If Rejected: Provides feedback to the nominator, explaining the reason for rejection. The system allows the nominator to submit a new nomination. 
    

-    Send Comms: Upon approval: 
    

-    Nominee is notified of the award (the nomination must be included). 
    

-    The Nominator is also notified that the award was approved. 
    

-    Dashboard: One record per nomination. Fields: ID, nominator, nominee, Practice, location, submit date, Nomination, review status, rejection reason, decision date, comms sent date. 
    

Key Features 

Submission: 

-    Structured form. WHAT and HOW fields. 
    

-    Mandatory fields. No blank submits. 
    

-    Nominator identity captured automatically. 
    

-    Block self-nomination 
    

-    Attach nominee, practice, location. 
    

AI tagging: 

-    Not employee status, routine task language, weak justification, repeat nominations from the previous quarter, and repeated nominations between the same individuals suggesting reciprocal nominations.  
    

-    Flags shown to coordinator, not blocking. 
    

Review: 

-    Coordinator sees full nomination plus flags. 
    

-    Completeness check, single click. 
    

-    Approve or reject decision. 
    

-    Rejection needs reason, mandatory field. 
    

-    Rejected nominator can resubmit. 
    

-    Resubmission links back to original ID. 
    

-    Audit log for coordinator decisions 
    

Approval, gift card (If possible): 

-    Approval triggers Reachdesk integration. 
    

-    Gift card issued automatically. 
    

-    Issuance date logged, no manual entry. 
    

-    Failed issuance triggers alert, retry. 
    

Comms: 

-    Nominee notified, nomination text included. 
    

-    Nominator notified, approval confirmed. 
    

-    Rejection feedback sent, reason included. 
    

-    All comms logged, with timestamp. 
    

Dashboard record: 

-    One row per nomination, always. 
    

-    Practice and location enable filtering.