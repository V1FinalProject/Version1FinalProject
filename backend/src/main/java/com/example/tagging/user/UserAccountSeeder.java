package com.example.tagging.user;

import com.example.tagging.auth.AccountRole;
import com.example.tagging.nomination.NominationStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the {@code users} collection once, the first time it's empty - same
 * pattern as {@link NominationStore}'s spreadsheet seed. Ten accounts: the
 * five real team members, the shared reviewer/coordinator account, and four
 * more reusing names that already appear as nominators/nominees in the seed
 * nominations, so the reviewer dashboard's "who nominated whom" panel has
 * real matches to show out of the box.
 *
 * Every password is {@code 1234} - hashed here via the injected
 * {@link PasswordEncoder} rather than a hardcoded hash, so it stays correct if
 * the encoder ever changes.
 */
@Component
public class UserAccountSeeder {

    private static final Logger log = LoggerFactory.getLogger(UserAccountSeeder.class);
    private static final String DEMO_PASSWORD = "1234";
    private static final String COMPANY = "Version 1";

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
                        "Consulting & Advisory", "Birmingham", hash, AccountRole.EMPLOYEE));

        repository.saveAll(accounts);
        log.info("Seeded {} user accounts.", accounts.size());
    }

    private static UserAccount account(String email, String firstName, String lastName, String jobTitle,
            String division, String department, String workLocation, String passwordHash, AccountRole role) {
        return new UserAccount(email, firstName, lastName, ContractType.PERMANENT, jobTitle, COMPANY, division,
                department, workLocation, passwordHash, role);
    }
}
