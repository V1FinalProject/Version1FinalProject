package com.example.tagging;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Headcount for one division or department, hand-authored rather than
 * counted from the (much smaller) set of seeded {@link UserAccount}s - see
 * "Team size / Company size" in {@code docs/nomination-detail-data-design.md}
 * for why a live count over our demo accounts wouldn't look realistic.
 *
 * Backs the reviewer detail panel's "Team size" (by department) and "Company
 * size" (by division, despite the name on the frontend - Version 1 doesn't
 * have real per-company data, so division is the closest meaningful tier).
 */
@Document(collection = "org_units")
public class OrgUnitSize {

    @Id
    private String id;
    private String name;
    private OrgUnitType type;
    private int size;

    public OrgUnitSize() {
    }

    public OrgUnitSize(String name, OrgUnitType type, int size) {
        this.id = type.name() + "::" + name;
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrgUnitType getType() {
        return type;
    }

    public void setType(OrgUnitType type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
