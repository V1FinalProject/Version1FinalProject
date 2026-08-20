package com.example.tagging;

/**
 * What the frontend's {@code AppUser} interface expects back from a
 * successful login - never the password hash. {@code role} and
 * {@code contractType} are lower-cased to match the TypeScript unions
 * (`'employee' | 'coordinator'`, `'permanent' | 'contractor'`) exactly.
 *
 * {@code contractType} rides along so the frontend can block a contractor
 * from the nomination form client-side too - the real enforcement is
 * {@link NominationStore#add}, this just avoids showing a contractor a form
 * they'll only be rejected from.
 */
public record AuthenticatedUser(String id, String name, String email, String practice, String location,
        String role, String contractType) {

    static AuthenticatedUser from(UserAccount account) {
        return new AuthenticatedUser(
                account.getId(),
                account.fullName(),
                account.getEmail(),
                account.getPractice(),
                account.getLocation(),
                account.getRole().name().toLowerCase(),
                account.getContractType().name().toLowerCase());
    }
}
