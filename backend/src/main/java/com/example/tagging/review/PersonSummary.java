package com.example.tagging.review;

import com.example.tagging.user.UserAccount;

/**
 * The extra account context the reviewer sees when expanding a nomination row
 * - enough to understand who the nominator and nominee actually are (job
 * title, department, contract type) beyond just their name.
 */
public record PersonSummary(String jobTitle, String department, String company, String workLocation,
        String contractType) {

    static PersonSummary from(UserAccount account) {
        return new PersonSummary(
                account.getJobTitle(),
                account.getDepartment(),
                account.getCompany(),
                account.getWorkLocation(),
                account.getContractType().name());
    }
}
