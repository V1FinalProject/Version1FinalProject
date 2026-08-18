package com.example.tagging;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// Placeholder only: there is no employee master data source yet, so this always
// passes (never flags) and treats everyone in the spreadsheet as a valid employee.
// Replace with a real lookup (e.g. HR system) once that data source exists.
@Component
public class NotEmployeeStatusChecker implements NominationFlagChecker {

    @Override
    public Optional<FlagResult> check(Nomination target, List<Nomination> allNominations) {
        return Optional.empty();
    }
}
