package com.example.tagging;

/**
 * One entry in the nominee picker - enough to identify them and, once the
 * nominator has typed a matching email, show a preview of who they are.
 */
public record NominatableUser(String name, String email, String department, String workLocation) {

    static NominatableUser from(UserAccount account) {
        return new NominatableUser(account.fullName(), account.getEmailAddress(), account.getDepartment(),
                account.getWorkLocation());
    }
}
