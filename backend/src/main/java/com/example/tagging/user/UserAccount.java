package com.example.tagging.user;

import com.example.tagging.auth.AccountRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A real Version 1 colleague who can sign in, submit nominations, and/or be
 * nominated. Login, and the reviewer dashboard's expanded detail (who
 * nominated whom, and their department), read from this collection.
 *
 * {@code emailAddress} is the natural unique key - every nomination already
 * carries nominator/nominee email, so no separate id scheme is needed to join
 * the two collections.
 */
@Document(collection = "users")
public class UserAccount {

    @Id
    private String emailAddress;
    private String firstName;
    private String lastName;
    private ContractType contractType;
    private String jobTitle;
    private String company;
    /**
     * The business division, e.g. {@code "Public Sector & Utilities (Ireland)"}
     * - one level up from {@link #department}, per the stakeholder's data
     * dictionary. Not surfaced on the frontend yet; seeded so it's there when
     * something needs it.
     */
    private String division;
    private String department;
    private String workLocation;
    /** BCrypt hash - never the raw password. */
    private String passwordHash;
    private AccountRole role;

    public UserAccount() {
    }

    public UserAccount(String emailAddress, String firstName, String lastName, ContractType contractType,
            String jobTitle, String company, String division, String department, String workLocation,
            String passwordHash, AccountRole role) {
        this.emailAddress = emailAddress.toLowerCase();
        this.firstName = firstName;
        this.lastName = lastName;
        this.contractType = contractType;
        this.jobTitle = jobTitle;
        this.company = company;
        this.division = division;
        this.department = department;
        this.workLocation = workLocation;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public String fullName() {
        return firstName + " " + lastName;
    }
}
