package com.example.tagging.auth;

/** Body of {@code POST /api/auth/login}. */
public record LoginRequest(String email, String password) {
}
