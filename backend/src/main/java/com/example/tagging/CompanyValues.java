package com.example.tagging;

import java.util.List;

// Version1's stated company values, shared by any checker that needs to compare
// nomination text against them.
public final class CompanyValues {

    public static final List<String> STATED_VALUES = List.of(
            "customer first",
            "excellence",
            "drive",
            "honesty & integrity",
            "honesty and integrity",
            "no ego",
            "personal commitment",
            "collaboration");

    private CompanyValues() {
    }
}
