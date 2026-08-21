package com.example.tagging;

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
                account("akos.bujdoso@version1.com", "Akos", "Bujdoso", "Associate Consultant", "IPSU",
                        "Dublin", hash, AccountRole.EMPLOYEE),
                account("leo.hickey@version1.com", "Leo", "Hickey", "Associate Consultant", "IPSU", "Dublin",
                        hash, AccountRole.EMPLOYEE),
                account("callum.oreilly@version1.com", "Callum", "O'Reilly", "Associate Consultant", "IPSU",
                        "Dublin", hash, AccountRole.EMPLOYEE),
                account("richard.herlihy@version1.com", "Richard", "Herlihy", "Associate Consultant", "IPSU",
                        "Dublin", hash, AccountRole.EMPLOYEE),
                account("luke.feeney@version1.com", "Luke", "Feeney", "Associate Consultant", "IPSU", "Dublin",
                        hash, AccountRole.EMPLOYEE),

                // Shared role account, not a person - see AccountRole and the
                // nominee-side check in NominationStore.add().
                account("reviewer@version1.com", "Star Award", "Reviewer", "Reviewer", "People & Culture",
                        "Dublin", hash, AccountRole.COORDINATOR),

                // Reuse names already present in the seed nominations dataset, so
                // the reviewer panel resolves real matches immediately.
                account("liam.quinn@version1.com", "Liam", "Quinn", "Senior Consultant",
                        "Digital, Data & Cloud", "Belfast", hash, AccountRole.EMPLOYEE),
                account("emer.powell@version1.com", "Emer", "Powell", "Consultant", "Enterprise Applications",
                        "Cork", hash, AccountRole.EMPLOYEE),
                account("sorcha.barry@version1.com", "Sorcha", "Barry", "Business Analyst", "Managed Services",
                        "London", hash, AccountRole.EMPLOYEE),
                account("conor.mccarthy@version1.com", "Conor", "McCarthy", "Technical Lead",
                        "Consulting & Advisory", "Birmingham", hash, AccountRole.EMPLOYEE));

        repository.saveAll(accounts);
        log.info("Seeded {} user accounts.", accounts.size());
    }

    private static UserAccount account(String email, String firstName, String lastName, String jobTitle,
            String department, String workLocation, String passwordHash, AccountRole role) {
        return new UserAccount(email, firstName, lastName, ContractType.PERMANENT, jobTitle, COMPANY, department,
                workLocation, passwordHash, role);
    }
}
