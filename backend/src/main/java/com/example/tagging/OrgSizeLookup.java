package com.example.tagging;

import org.springframework.stereotype.Service;

/**
 * Looks up the headcount stats for the reviewer detail panel: "Team size" by
 * department, "Company size" by division (see {@link OrgUnitSize}'s
 * Javadoc for why division, not a literal company).
 *
 * Null when there's no matching row - shown as the same em dash placeholder
 * the frontend already had before these stats existed.
 */
@Service
public class OrgSizeLookup {

    private final OrgUnitSizeRepository repository;

    public OrgSizeLookup(OrgUnitSizeRepository repository) {
        this.repository = repository;
    }

    public Integer teamSizeFor(String department) {
        return sizeOf(department, OrgUnitType.DEPARTMENT);
    }

    public Integer companySizeFor(String division) {
        return sizeOf(division, OrgUnitType.DIVISION);
    }

    private Integer sizeOf(String name, OrgUnitType type) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return repository.findByNameAndType(name, type).map(OrgUnitSize::getSize).orElse(null);
    }
}
