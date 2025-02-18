package com.indium.meetingroombooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents the details of a location within the meeting room booking system.
 */
@Entity
@Table(name = "location_details")
public class LocationDetails {

    /**
     * Unique identifier for the location.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Short locationId;

    /**
     * Full name of the location.
     */
    @Column(name = "location_name", nullable = false, length = 50)
    private String locationName;

    /**
     * Short name or abbreviation of the location.
     */
    @Column(name = "short_location_name", nullable = false, length = 20)
    private String shortLocationName;

    /**
     * Indicates whether the location is active ('y' or 'n').
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "CHAR(1) CHECK (is_active IN ('y', 'n'))")
    private char isActive;

    /**
     * The user who created the location entry.
     */
    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "user_id", nullable = true)
    private User createdBy;

    /**
     * The date and time when the location entry was created.
     */
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    /**
     * List of admin users for the location, stored as a JSON string.
     */
    @Column(name = "admin_users", columnDefinition = "JSON")
    private String adminUsers;

    /**
     * The number of days allowed for booking in advance.
     */
    @Column(name = "booking_allowed_window_in_days")
    private Short bookingAllowedWindowInDays;

    /**
     * The notice duration required before booking (in minutes).
     */
    @Column(name = "notice_duration_to_book_in_min")
    private Byte noticeDurationToBookInMin;

    /**
     * The number of recurring bookings allowed for the location.
     */
    @Column(name = "recurrence_count_allowed")
    private Byte recurrenceCountAllowed;

    // Getters and Setters

    public Short getLocationId() {
        return locationId;
    }

    public void setLocationId(Short locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getShortLocationName() {
        return shortLocationName;
    }

    public void setShortLocationName(String shortLocationName) {
        this.shortLocationName = shortLocationName;
    }

    public char getIsActive() {
        return isActive;
    }

    public void setIsActive(char isActive) {
        this.isActive = isActive;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getAdminUsers() {
        return adminUsers;
    }

    public void setAdminUsers(String adminUsers) {
        this.adminUsers = adminUsers;
    }

    public Short getBookingAllowedWindowInDays() {
        return bookingAllowedWindowInDays;
    }

    public void setBookingAllowedWindowInDays(Short bookingAllowedWindowInDays) {
        this.bookingAllowedWindowInDays = bookingAllowedWindowInDays;
    }

    public Byte getNoticeDurationToBookInMin() {
        return noticeDurationToBookInMin;
    }

    public void setNoticeDurationToBookInMin(Byte noticeDurationToBookInMin) {
        this.noticeDurationToBookInMin = noticeDurationToBookInMin;
    }

    public Byte getRecurrenceCountAllowed() {
        return recurrenceCountAllowed;
    }

    public void setRecurrenceCountAllowed(Byte recurrenceCountAllowed) {
        this.recurrenceCountAllowed = recurrenceCountAllowed;
    }
}
