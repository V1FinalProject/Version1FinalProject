package com.example.tagging;

/**
 * What an account can do once signed in: nominate colleagues, or review
 * nominations on the coordinator dashboard. Independent of {@link ContractType}
 * - a permanent employee is the normal case for either role.
 */
public enum AccountRole {
    EMPLOYEE,
    COORDINATOR
}
