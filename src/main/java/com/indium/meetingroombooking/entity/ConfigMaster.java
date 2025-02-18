package com.indium.meetingroombooking.entity;

import jakarta.persistence.*;

/**
 * Represents the configuration master entity used for storing configuration parameters
 * and associated employee details within the meeting room booking system.
 */
@Entity
@Table(name = "config_master")
public class ConfigMaster {

    /**
     * Unique identifier for the configuration record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_master_id")
    private byte configMasterId;

    /**
     * The configuration parameter name (e.g., "meetingroombooking_owner").
     */
    @Column(name = "config_param", nullable = false)
    private String configParam;

    /**
     * JSON representation of employee user IDs associated with the configuration.
     */
    @Column(name = "config_employee_code", columnDefinition = "json", nullable = false)
    private String configEmployeeCode;

    // Getters and Setters
    public byte getConfigMasterId() {
        return configMasterId;
    }

    public void setConfigMasterId(byte configMasterId) {
        this.configMasterId = configMasterId;
    }

    public String getConfigParam() {
        return configParam;
    }

    public void setConfigParam(String configParam) {
        this.configParam = configParam;
    }

    public String getConfigEmployeeCode() {
        return configEmployeeCode;
    }

    public void setConfigEmployeeCode(String configEmployeeCode) {
        this.configEmployeeCode = configEmployeeCode;
    }
}
