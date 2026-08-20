package com.example.tagging;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A real Version 1 colleague who can sign in, submit nominations, and/or be
 * nominated. Replaces the frontend's hardcoded {@code DEMO_USERS} list - login,
 * the nominee picker, and the reviewer dashboard's expanded detail all read
 * from this collection now.
 *
 * {@code id} is the email address: it's already the natural unique key, and
 * every nomination already carries nominator/nominee email, so no separate id
 * scheme is needed to join the two collections.
 */
@Document(collection = "users")
public class UserAccount {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    /** BCrypt hash - never the raw password. */
    private String passwordHash;
    private String practice;
    private String location;
    private ContractType contractType;
    private String company;
    private String department;
    private String jobTitle;
    private AccountRole role;

    public UserAccount() {
    }

    public UserAccount(String firstName, String lastName, String email, String passwordHash,
            String practice, String location, ContractType contractType, String company,
            String department, String jobTitle, AccountRole role) {
        this.id = email.toLowerCase();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.practice = practice;
        this.location = location;
        this.contractType = contractType;
        this.company = company;
        this.department = department;
        this.jobTitle = jobTitle;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPractice() {
        return practice;
    }

    public void setPractice(String practice) {
        this.practice = practice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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
