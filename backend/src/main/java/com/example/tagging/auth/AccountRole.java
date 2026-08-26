package com.example.tagging.auth;

import com.example.tagging.nomination.NominationStore;

/**
 * What an account can do once signed in: nominate colleagues, or review
 * nominations on the coordinator dashboard. There's exactly one COORDINATOR
 * account today ({@code reviewer@version1.com}) - a shared role account
 * rather than a real person, so it can't itself be nominated (see
 * {@link NominationStore#add}).
 */
public enum AccountRole {
    EMPLOYEE,
    COORDINATOR
}
