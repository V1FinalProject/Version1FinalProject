package com.example.tagging;

/** Body of {@code POST /api/auth/login}. */
public record LoginRequest(String email, String password) {
}
