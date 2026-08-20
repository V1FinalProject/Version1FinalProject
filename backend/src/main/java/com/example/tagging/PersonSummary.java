package com.example.tagging;

/**
 * The extra account context the reviewer sees when expanding a nomination row
 * - enough to understand who the nominator and nominee actually are (contract
 * type, company, department, job title) beyond just their name.
 */
public record PersonSummary(String contractType, String company, String department, String jobTitle,
        String location) {

    static PersonSummary from(UserAccount account) {
        return new PersonSummary(
                account.getContractType().name(),
                account.getCompany(),
                account.getDepartment(),
                account.getJobTitle(),
                account.getLocation());
    }
}
