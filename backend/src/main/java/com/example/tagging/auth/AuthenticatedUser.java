package com.example.tagging.auth;

import com.example.tagging.user.UserAccount;

/**
 * What the frontend's {@code AppUser} interface expects back from a
 * successful login - never the password hash. {@code role} is lower-cased to
 * match the TypeScript union (`'employee' | 'coordinator'`) exactly.
 *
 * {@code practice}/{@code location} map onto the account's
 * {@code department}/{@code workLocation} - `AppUser` already has those two
 * field names (the nomination form stamps them onto every submission), so
 * reusing them here means nothing downstream of login needs to change.
 */
public record AuthenticatedUser(String id, String name, String email, String practice, String location,
        String role) {

    static AuthenticatedUser from(UserAccount account) {
        return new AuthenticatedUser(
                account.getEmailAddress(),
                account.fullName(),
                account.getEmailAddress(),
                account.getDepartment(),
                account.getWorkLocation(),
                account.getRole().name().toLowerCase());
    }
}
