package com.example.tagging.review;

import com.example.tagging.org.OrgSizeLookup;
import com.example.tagging.org.OrgUnitSize;
import com.example.tagging.user.UserAccount;

/**
 * The extra account context the reviewer sees when expanding a nomination row
 * - enough to understand who the nominator and nominee actually are (job
 * title, department, contract type) beyond just their name.
 *
 * {@code companySize} is sourced from the account's division, not a literal
 * company field - see {@link OrgUnitSize}'s Javadoc.
 */
public record PersonSummary(String jobTitle, String department, String company, String workLocation,
        String contractType, Integer teamSize, Integer companySize) {

    static PersonSummary from(UserAccount account, OrgSizeLookup orgSizes) {
        return new PersonSummary(
                account.getJobTitle(),
                account.getDepartment(),
                account.getCompany(),
                account.getWorkLocation(),
                account.getContractType().name(),
                orgSizes.teamSizeFor(account.getDepartment()),
                orgSizes.companySizeFor(account.getDivision()));
    }
}
