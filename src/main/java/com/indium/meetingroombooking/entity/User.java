package com.indium.meetingroombooking.entity;

import jakarta.persistence.*;

/**
 * Represents a user within the system, with attributes for employee details, role flags, and login-related information.
 */
@Entity
@Table(name = "user")
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Unique employee code for the user.
     */
    @Column(name = "employee_code", nullable = false, length = 50)
    private String employeeCode;

    /**
     * Company email associated with the user.
     */
    @Column(name = "company_email", nullable = false, unique = true, length = 100)
    private String companyEmail;

    /**
     * Designation of the user within the company.
     */
    @Column(name = "designation", length = 50)
    private String designation;

    /**
     * Department to which the user belongs.
     */
    @Column(name = "department", length = 50)
    private String department;

    /**
     * Token used for login purposes.
     */
    @Column(name = "login_token", length = 255)
    private String loginToken;

    /**
     * Current status of the employee (e.g., active, inactive).
     */
    @Column(name = "employee_status", length = 50)
    private String employeeStatus;

    /**
     * Flag indicating whether the user has admin privileges (1 for yes, 0 for no).
     */
    @Column(name = "admin_flag", nullable = false)
    private Byte adminFlag;

    /**
     * Flag indicating whether the user can log in to the admin portal (1 for yes, 0 for no).
     */
    @Column(name = "admin_portal_login_flag", nullable = false)
    private Byte adminPortalLoginFlag;

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public String getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(String employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public Byte getAdminFlag() {
        return adminFlag;
    }

    public void setAdminFlag(Byte adminFlag) {
        this.adminFlag = adminFlag;
    }

    public Byte getAdminPortalLoginFlag() {
        return adminPortalLoginFlag;
    }

    public void setAdminPortalLoginFlag(Byte adminPortalLoginFlag) {
        this.adminPortalLoginFlag = adminPortalLoginFlag;
    }
}
