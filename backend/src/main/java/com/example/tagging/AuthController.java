package com.example.tagging;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Real login against {@link UserAccount}.
 *
 * There is no real identity provider yet (that's Microsoft SSO's job in a real
 * deployment - see the login page), so this is intentionally minimal: check
 * the password, hand back the user. No session/token - the frontend keeps the
 * signed-in user in its own state afterward.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserAccountRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthenticatedUser login(@RequestBody LoginRequest request) {
        UserAccount account = users.findByEmailAddressIgnoreCase(safe(request.email()))
                .orElseThrow(AuthController::badCredentials);

        if (!passwordEncoder.matches(safe(request.password()), account.getPasswordHash())) {
            throw badCredentials();
        }

        return AuthenticatedUser.from(account);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static ResponseStatusException badCredentials() {
        // Same message either way - don't reveal whether the email exists.
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect email or password");
    }
}
