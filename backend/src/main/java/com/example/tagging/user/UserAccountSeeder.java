package com.example.tagging.user;

import com.example.tagging.auth.AccountRole;
import com.example.tagging.nomination.NominationStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Seeds the {@code users} collection once, the first time it's empty - same
 * pattern as {@link NominationStore}'s spreadsheet seed.
 *
 * Around 90 accounts: the original ten, ~26 fictional Q4 seed nominees/
 * nominators backfilled with invented (but plausible) profiles, ~50 real Q3
 * nominators/nominees sourced from the Version 1 employee directory, one more
 * real employee who happens to share a name with a Q4 seed nominee, and five
 * dedicated accounts that exist purely to demonstrate the reciprocity stat.
 * Every nominator/nominee email in the seeded nominations resolves to a real
 * account here - see {@code docs/nomination-detail-data-design.md} for the
 * full reasoning.
 *
 * Every password is {@code 1234} - hashed here via the injected
 * {@link PasswordEncoder} rather than a hardcoded hash, so it stays correct if
 * the encoder ever changes.
 */
@Component
public class UserAccountSeeder {

    private static final Logger log = LoggerFactory.getLogger(UserAccountSeeder.class);
    private static final String DEMO_PASSWORD = "1234";
    /**
     * Placeholder company for accounts with no identifiable external client -
     * Version 1 doesn't have real per-client data outside Public Sector, so
     * naming the employer itself is the least misleading fallback.
     */
    private static final String COMPANY = "Version 1";

    /** The specific business unit ({@code account()}'s {@code division} argument) that gets generalised to {@link #GENERAL_PUBLIC_SECTOR_DIVISION}. */
    private static final String PUBLIC_SECTOR_BUSINESS_UNIT = "Public Sector & Utilities (Ireland)";
    private static final String GENERAL_PUBLIC_SECTOR_DIVISION = "Public Sector";

    /**
     * Department -&gt; real client organisation, for the Public Sector
     * departments actually used below - see
     * {@code docs/nomination-detail-data-design.md}. Sourced from the
     * "Department" column of the Star Awards spreadsheet, which for Public
     * Sector rows is genuinely {@code "IPSU <client> [<sub-team>]"}.
     * {@code "IPSU Public Core"} is deliberately omitted - it's an internal
     * delivery pool, not a named client - and falls back to
     * {@link #GENERAL_PUBLIC_SECTOR_DIVISION} below.
     */
    private static final Map<String, String> PUBLIC_SECTOR_CLIENTS = Map.of(
            "IPSU Dept of Justice", "Dept of Justice",
            "IPSU DAFM Payments AgSchemes", "DAFM",
            "IPSU DAFM TLM International Trade", "DAFM",
            "IPSU DAERA", "DAERA",
            "IPSU ESB", "ESB");

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountSeeder(UserAccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    void seed() {
        if (repository.count() > 0) {
            return;
        }

        String hash = passwordEncoder.encode(DEMO_PASSWORD);

        List<UserAccount> accounts = List.of(
                account("akos.bujdoso@version1.com", "Akos", "Bujdoso", "Associate Consultant",
                        "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash,
                        AccountRole.EMPLOYEE),
                account("leo.hickey@version1.com", "Leo", "Hickey", "Associate Consultant",
                        "Public Sector & Utilities (Ireland)", "IPSU DAFM Payments AgSchemes", "Dublin", hash,
                        AccountRole.EMPLOYEE),
                account("callum.oreilly@version1.com", "Callum", "O'Reilly", "Associate Consultant",
                        "Public Sector & Utilities (Ireland)", "IPSU DAFM TLM International Trade", "Dublin", hash,
                        AccountRole.EMPLOYEE),
                account("richard.herlihy@version1.com", "Richard", "Herlihy", "Associate Consultant",
                        "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash,
                        AccountRole.EMPLOYEE),
                account("luke.feeney@version1.com", "Luke", "Feeney", "Associate Consultant",
                        "Public Sector & Utilities (Ireland)", "IPSU DAFM Payments AgSchemes", "Dublin", hash,
                        AccountRole.EMPLOYEE),

                // Shared role account, not a person - see AccountRole and the
                // nominee-side check in NominationStore.add(). "People & Culture"
                // isn't one of the stakeholder's division examples, so this is
                // parked under Business Support (alongside Administration, IT
                // Services & Operations) as the closest fit - flag if there's a
                // dedicated HR division in the real dictionary.
                account("reviewer@version1.com", "Star Award", "Reviewer", "Reviewer", "Business Support",
                        "People & Culture", "Dublin", hash, AccountRole.COORDINATOR),

                // Reuse names already present in the seed nominations dataset, so
                // the reviewer panel resolves real matches immediately.
                account("liam.quinn@version1.com", "Liam", "Quinn", "Senior Consultant",
                        "Digital, Data & Cloud", "DDC Cloud AWS Jaguar", "Belfast", hash, AccountRole.EMPLOYEE),
                account("emer.powell@version1.com", "Emer", "Powell", "Consultant", "Enterprise Applications",
                        "EA MS Cloud Europe", "Cork", hash, AccountRole.EMPLOYEE),
                // "Managed Services" isn't in the stakeholder's division list either
                // - best guess is Services Reliability Group (the closest ops/
                // platform-support division on offer). Worth confirming.
                account("sorcha.barry@version1.com", "Sorcha", "Barry", "Business Analyst",
                        "Services Reliability Group", "Managed Services", "London", hash, AccountRole.EMPLOYEE),
                // Same story - "Consulting & Advisory" has no clean match in the
                // given list; parked under Business Support as a guess.
                account("conor.mccarthy@version1.com", "Conor", "McCarthy", "Technical Lead", "Business Support",
                        "Consulting & Advisory", "Birmingham", hash, AccountRole.EMPLOYEE),

            // --- Backfilled Q4 seed nominees/nominators (fictional, no real directory match) ---
            account("aine.sheehan@version1.com", "Aine", "Sheehan", "Associate Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash, AccountRole.EMPLOYEE),
            account("aisling.fitzgerald@version1.com", "Aisling", "Fitzgerald", "Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU DAFM Payments AgSchemes", "Dublin", hash, AccountRole.EMPLOYEE),
            account("aoife.byrne@version1.com", "Aoife", "Byrne", "Senior Consultant",
                    "Digital, Data & Cloud", "DDC Cloud AWS Jaguar", "Belfast", hash, AccountRole.EMPLOYEE),
            account("brid.healy@version1.com", "Brid", "Healy", "Business Analyst",
                    "Digital, Data & Cloud", "DDC Private Sector Management", "Edinburgh", hash, AccountRole.EMPLOYEE),
            account("cathal.dunne@version1.com", "Cathal", "Dunne", "Technical Lead",
                    "Enterprise Applications", "EA MS Cloud Europe", "Belfast", hash, AccountRole.EMPLOYEE),
            account("cian.walsh@version1.com", "Cian", "Walsh", "Associate Consultant",
                    "Business Support", "Comm Deal Execution", "Belfast", hash, AccountRole.EMPLOYEE),
            account("ciara.lynch@version1.com", "Ciara", "Lynch", "Consultant",
                    "Business Support", "Administration", "Birmingham", hash, AccountRole.EMPLOYEE),
            account("clodagh.farrell@version1.com", "Clodagh", "Farrell", "Senior Consultant",
                    "Services Reliability Group", "SRG Platform Services OCI & AWS", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("cormac.gallagher@version1.com", "Cormac", "Gallagher", "Business Analyst",
                    "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash, AccountRole.EMPLOYEE),
            account("darragh.ryan@version1.com", "Darragh", "Ryan", "Technical Lead",
                    "Public Sector & Utilities (Ireland)", "IPSU DAFM Payments AgSchemes", "Dublin", hash, AccountRole.EMPLOYEE),
            account("diarmuid.hughes@version1.com", "Diarmuid", "Hughes", "Associate Consultant",
                    "Digital, Data & Cloud", "DDC Cloud AWS Jaguar", "Belfast", hash, AccountRole.EMPLOYEE),
            account("eoin.sullivan@version1.com", "Eoin", "Sullivan", "Consultant",
                    "Digital, Data & Cloud", "DDC Private Sector Management", "Edinburgh", hash, AccountRole.EMPLOYEE),
            account("fionn.brennan@version1.com", "Fionn", "Brennan", "Senior Consultant",
                    "Enterprise Applications", "EA MS Cloud Europe", "Belfast", hash, AccountRole.EMPLOYEE),
            account("grace.obrien@version1.com", "Grace", "O'Brien", "Business Analyst",
                    "Business Support", "Comm Deal Execution", "Belfast", hash, AccountRole.EMPLOYEE),
            account("joe.duffy@version1.com", "Joe", "Duffy", "Technical Lead",
                    "Business Support", "Administration", "Birmingham", hash, AccountRole.EMPLOYEE),
            account("maeve.reilly@version1.com", "Maeve", "Reilly", "Associate Consultant",
                    "Services Reliability Group", "SRG Platform Services OCI & AWS", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("niall.burke@version1.com", "Niall", "Burke", "Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash, AccountRole.EMPLOYEE),
            account("niamh.kelly@version1.com", "Niamh", "Kelly", "Senior Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU DAFM Payments AgSchemes", "Dublin", hash, AccountRole.EMPLOYEE),
            account("oisin.flynn@version1.com", "Oisin", "Flynn", "Business Analyst",
                    "Digital, Data & Cloud", "DDC Cloud AWS Jaguar", "Belfast", hash, AccountRole.EMPLOYEE),
            account("orla.nolan@version1.com", "Orla", "Nolan", "Technical Lead",
                    "Digital, Data & Cloud", "DDC Private Sector Management", "Edinburgh", hash, AccountRole.EMPLOYEE),
            account("padraig.connolly@version1.com", "Padraig", "Connolly", "Associate Consultant",
                    "Enterprise Applications", "EA MS Cloud Europe", "Belfast", hash, AccountRole.EMPLOYEE),
            account("roisin.doyle@version1.com", "Roisin", "Doyle", "Consultant",
                    "Business Support", "Comm Deal Execution", "Belfast", hash, AccountRole.EMPLOYEE),
            account("ronan.kennedy@version1.com", "Ronan", "Kennedy", "Senior Consultant",
                    "Business Support", "Administration", "Birmingham", hash, AccountRole.EMPLOYEE),
            account("saoirse.doherty@version1.com", "Saoirse", "Doherty", "Business Analyst",
                    "Services Reliability Group", "SRG Platform Services OCI & AWS", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("sinead.murray@version1.com", "Sinead", "Murray", "Technical Lead",
                    "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash, AccountRole.EMPLOYEE),
            account("tadhg.moran@version1.com", "Tadhg", "Moran", "Associate Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU DAFM Payments AgSchemes", "Dublin", hash, AccountRole.EMPLOYEE),

            // --- Real employee, sourced from the Version 1 directory (matches a Q4 seed nominee) ---
            account("sean.murphy@version1.com", "Sean", "Murphy", "Associate Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU Public Core", "Dublin", hash, AccountRole.EMPLOYEE),

            // --- Real Q3 nominators/nominees, sourced from the Version 1 directory ---
            account("nagina.bibi@version1.com", "Nagina", "Bibi", "Associate Consultant",
                    "Digital, Data & Cloud", "DDC Cloud AWS Jaguar", "Belfast", hash, AccountRole.EMPLOYEE),
            account("sara.wallace@version1.com", "Sara", "Wallace", "AWS Engineer",
                    "Digital, Data & Cloud", "DDC Cloud AWS Jaguar", "Belfast", hash, AccountRole.EMPLOYEE),
            account("andy.holly@version1.com", "Andrew", "Holly", "Head of Workplace Operations",
                    "Business Support", "Administration", "Birmingham", hash, AccountRole.EMPLOYEE),
            account("grace.bishton@version1.com", "Grace", "Bishton", "Workplace Operations Assistant",
                    "Business Support", "Administration", "Birmingham", hash, AccountRole.EMPLOYEE),
            account("michael.dakin@version1.com", "Michael", "Dakin", "Oracle ERP Project Manager",
                    "Enterprise Applications", "EA APP INTG Ext & Intg Europe", "London", hash, AccountRole.EMPLOYEE),
            account("jitha.easo@version1.com", "Jitha", "Easo", "Senior Oracle Cloud OIC Technical Consultant Lead",
                    "Enterprise Applications", "EA APP INTG Ext & Intg Europe", "London", hash, AccountRole.EMPLOYEE),
            account("swetha.sekar@version1.com", "Swetha", "Sekar", "OIC Developer",
                    "Enterprise Applications", "EA MS Cloud Europe", "Belfast", hash, AccountRole.EMPLOYEE),
            account("roopak.nair@version1.com", "Roopak", "Ramachandran Nair", "Oracle Technology Lead - Cloud Managed Service",
                    "Enterprise Applications", "EA MS Cloud Europe", "Belfast", hash, AccountRole.EMPLOYEE),
            account("adam.muir@version1.com", "Adam", "Muir", "Head of Commercial Pricing",
                    "Business Support", "Comm Deal Execution", "Belfast", hash, AccountRole.EMPLOYEE),
            account("hannah.henry@version1.com", "Hannah", "Henry", "Bid Administrator",
                    "Business Support", "Comm Deal Execution", "Belfast", hash, AccountRole.EMPLOYEE),
            account("kalyan.reddy@version1.com", "Kalyan", "Reddy", "Assistant Manager",
                    "India Delivery Centre", "Bangalore IDC Business Support", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("sujay.hebbalmatt@version1.com", "Sujay", "Matt", "Accounts  Assistant",
                    "India Delivery Centre", "Bangalore IDC Business Support", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("joanne.collins@version1.com", "Joanne", "Collins", "Enterprise Architect",
                    "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash, AccountRole.EMPLOYEE),
            account("etienne.poisson@version1.com", "Etienne", "Poisson", "Applications Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash, AccountRole.EMPLOYEE),
            account("karen.poynton@version1.com", "Karen", "Poynton", "Project Manager",
                    "SAM & Licence Management", "SAM Core", "Birmingham", hash, AccountRole.EMPLOYEE),
            account("william.nelson@version1.com", "William", "Nelson", "Sales Specialist/License Management Practice",
                    "SAM & Licence Management", "SAM Core", "Birmingham", hash, AccountRole.EMPLOYEE),
            account("stephen.cranfield@version1.com", "Stephen", "Cranfield", "Senior Security Governance Specialist",
                    "Business Support", "IT Services & Operations", "Dublin", hash, AccountRole.EMPLOYEE),
            account("sandeep.sharma@version1.com", "Sandeep", "Sharma", "IAM and M365 Engineer",
                    "Business Support", "IT Services & Operations", "Dublin", hash, AccountRole.EMPLOYEE),
            account("sonia.k@version1.com", "Sonia", "K", "PMO Lead",
                    "Business Support", "GCC Growth", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("shilpa.bhaskaran@version1.com", "Shilpa", "Bhaskaran", "ESG - Employee Experience Consultant, Senior",
                    "Business Support", "GCC Growth", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("ammu.kuriakose@version1.com", "Ammu", "Kuriakose", "QA/Test Analyst",
                    "Public Sector & Utilities (Ireland)", "IPSU DAFM Payments AgSchemes", "Dublin", hash, AccountRole.EMPLOYEE),
            account("rohit.wadi@version1.com", "Rohit", "Wadi", "QA/Test Analyst",
                    "Public Sector & Utilities (Ireland)", "IPSU DAFM Payments AgSchemes", "Dublin", hash, AccountRole.EMPLOYEE),
            account("gabriela.holzel@version1.com", "Gabriela", "Holzel", "Associate Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash, AccountRole.EMPLOYEE),
            account("jacek.eisenbart@version1.com", "Jacek", "Eisenbart", "Senior Data Engineer",
                    "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash, AccountRole.EMPLOYEE),
            account("manish.achar@version1.com", "Manish", "Achar", "Junior Accounts Assistant",
                    "India Delivery Centre", "Bangalore IDC Business Support", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("uttam.yadav@version1.com", "Uttam", "Yadav", "Infrastructure Support & Helpdesk",
                    "Services Reliability Group", "SRG Platform Services OCI & AWS", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("suraj.singh@version1.com", "Suraj", "Singh", "Infrastructure Support & Helpdesk",
                    "Services Reliability Group", "SRG Platform Services OCI & AWS", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("donagh.noone@version1.com", "Donagh", "Noone", "Portfolio Director, Technology",
                    "Public Sector & Utilities (Ireland)", "IPSU DAFM TLM International Trade", "Dublin", hash, AccountRole.EMPLOYEE),
            account("sachin.langute@version1.com", "Sachin", "Langute", "Oracle Technical Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU DAFM TLM International Trade", "Dublin", hash, AccountRole.EMPLOYEE),
            account("jillian.giles@version1.com", "Jillian", "Giles", "Account Manager - Public Sector",
                    "Business Support", "Comm Deal Execution", "Belfast", hash, AccountRole.EMPLOYEE),
            account("ross.hunter@version1.com", "Ross", "Hunter", "Senior Bid Manager",
                    "Business Support", "Comm Deal Execution", "Belfast", hash, AccountRole.EMPLOYEE),
            account("arabinda.das@version1.com", "Arabinda", "Das", "Business Analyst",
                    "Digital, Data & Cloud", "DDC Private Sector Management", "Edinburgh", hash, AccountRole.EMPLOYEE),
            account("jocelyn.greer@version1.com", "Jocelyn", "Greer", "Senior Delivery Manager",
                    "Digital, Data & Cloud", "DDC Private Sector Management", "Edinburgh", hash, AccountRole.EMPLOYEE),
            account("karthik.srinivas@version1.com", "Karthik", "Srinivas", "Platforms Team Lead",
                    "Services Reliability Group", "SRG Platform Services OCI & AWS", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("vinay.konganapalli@version1.com", "Vinay", "Konganapalli", "AWS Cloud Engineer",
                    "Services Reliability Group", "SRG Platform Services OCI & AWS", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("gearoid.omahony@version1.com", "Gearoid", "O'Mahony", "Managed Services Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU ESB", "London", hash, AccountRole.EMPLOYEE),
            account("sean.breslin@version1.com", "Sean", "Breslin", "Managed Services Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU ESB", "London", hash, AccountRole.EMPLOYEE),
            account("apoorv.sunger@version1.com", "Apoorv", "Sunger", "Senior .NET Developer - Full Stack",
                    "Business Support", "Corporate", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("sijomalayil.thomas@version1.com", "Sijo Malayil", "Thomas", "Travel & Events Lead (IDC)",
                    "Business Support", "Corporate", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("mathivanan.e@version1.com", "Mathivanan", "E", "Service Desk Analyst",
                    "Services Reliability Group", "ASPIRE GSC Service Desk", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("hemanth.kumar@version1.com", "Hemanth", "Kumar", "Service Desk Team Lead",
                    "Services Reliability Group", "ASPIRE GSC Service Desk", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("avril.vera@version1.com", "Avril", "Vera Leon", "Senior Pre-Sales Consultant",
                    "Business Support", "Comm Deal Execution", "Belfast", hash, AccountRole.EMPLOYEE),
            account("sinead.gallagher@version1.com", "Sinead", "Gallagher", "Bid Manager",
                    "Business Support", "Comm Deal Execution", "Belfast", hash, AccountRole.EMPLOYEE),
            account("mukesh.garg@version1.com", "Mukesh", "Garg", "IT Operations Engineer",
                    "Services Reliability Group", "SRG Platform Services OCI & AWS", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("grant.raiker@version1.com", "Grant", "Raiker", "Application Support Consultant",
                    "Services Reliability Group", "ASPIRE GSC Service Desk", "Edinburgh", hash, AccountRole.EMPLOYEE),
            account("jaden.atkinson@version1.com", "Jaden", "Atkinson", "Performance Engineer",
                    "Services Reliability Group", "ASPIRE GSC Service Desk", "Edinburgh", hash, AccountRole.EMPLOYEE),
            account("ed.elwood@version1.com", "Ed", "Elwood", "Senior Consultant",
                    "Services Reliability Group", "SRG Applications Microsoft 2", "Belfast", hash, AccountRole.EMPLOYEE),
            account("joshua.mcroberts@version1.com", "Joshua", "Mcroberts", "Applications Consultant",
                    "Services Reliability Group", "SRG Applications Microsoft 2", "Belfast", hash, AccountRole.EMPLOYEE),
            account("mohamed.irfan@version1.com", "Mohamed", "Irfan", "Senior Tester",
                    "Digital, Data & Cloud", "DDC Digital Quality Engineering", "Bangalore", hash, AccountRole.EMPLOYEE),
            account("manoj.a@version1.com", "Manoj", "A", "QA/Test Engineer",
                    "Digital, Data & Cloud", "DDC Digital Quality Engineering", "Bangalore", hash, AccountRole.EMPLOYEE),

            // --- Dedicated reciprocity-demo accounts (see docs/nomination-detail-data-design.md) ---
            account("roland.fitzsimons@version1.com", "Roland", "Fitzsimons", "Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash, AccountRole.EMPLOYEE),
            account("deirdre.nash@version1.com", "Deirdre", "Nash", "Senior Consultant",
                    "Public Sector & Utilities (Ireland)", "IPSU Dept of Justice", "Dublin", hash, AccountRole.EMPLOYEE),
            account("marcus.whelan@version1.com", "Marcus", "Whelan", "Consultant",
                    "Digital, Data & Cloud", "DDC Cloud AWS Jaguar", "Belfast", hash, AccountRole.EMPLOYEE),
            account("priya.chandran@version1.com", "Priya", "Chandran", "Senior Consultant",
                    "Digital, Data & Cloud", "DDC Cloud AWS Jaguar", "Belfast", hash, AccountRole.EMPLOYEE),
            account("fintan.casey@version1.com", "Fintan", "Casey", "Associate Consultant",
                    "Digital, Data & Cloud", "DDC Cloud AWS Jaguar", "Belfast", hash, AccountRole.EMPLOYEE));

        repository.saveAll(accounts);
        log.info("Seeded {} user accounts.", accounts.size());
    }

    private static UserAccount account(String email, String firstName, String lastName, String jobTitle,
            String division, String department, String workLocation, String passwordHash, AccountRole role) {
        boolean publicSector = PUBLIC_SECTOR_BUSINESS_UNIT.equals(division);
        String resolvedDivision = publicSector ? GENERAL_PUBLIC_SECTOR_DIVISION : division;
        String businessUnit = publicSector ? PUBLIC_SECTOR_BUSINESS_UNIT : null;
        String company = publicSector
                ? PUBLIC_SECTOR_CLIENTS.getOrDefault(department, GENERAL_PUBLIC_SECTOR_DIVISION)
                : COMPANY;
        return new UserAccount(email, firstName, lastName, ContractType.PERMANENT, jobTitle, company,
                resolvedDivision, businessUnit, department, workLocation, passwordHash, role);
    }
}
