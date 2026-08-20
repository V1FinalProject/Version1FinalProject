package com.example.tagging;

/**
 * Whether a colleague is a permanent employee or a contractor. Contractors
 * cannot submit or receive Star Award nominations - see the checks in
 * {@link NominationStore#add}.
 */
public enum ContractType {
    PERMANENT,
    CONTRACTOR
}
