package com.indium.meetingroombooking.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
/**
 * Represents the details of a room within a location.
 */
@Entity
@Table(name = "room_details")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RoomDetails {

    /**
     * Unique identifier for the room.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Short roomId;

    /**
     * Name of the room.
     */
    @Column(name = "room_name", nullable = false, length = 100)
    private String roomName;

    /**
     * Indicates whether an external display is available in the room ('y' or 'n').
     */
    @Column(name = "external_display_availability", nullable = false, columnDefinition = "CHAR(1) CHECK (external_display_availability IN ('y', 'n'))")
    private Character externalDisplayAvailability;

    /**
     * Facilities available in the room, stored as a JSON string.
     */
    @Column(name = "facilities", nullable = false, columnDefinition = "JSON")
    private String facilities;

    /**
     * Maximum capacity of the room.
     */
    @Column(name = "capacity", nullable = false)
    private Short capacity;

    /**
     * The location to which this room belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationDetails locationId;

    /**
     * Indicates whether booking is allowed for the room ('y' or 'n').
     */
    @Column(name = "booking_allowed", columnDefinition = "CHAR(1) CHECK (booking_allowed IN ('y', 'n'))")
    private Character bookingAllowed;

    /**
     * Indicates whether approval is needed for booking the room ('y' or 'n').
     */
    @Column(name = "is_approval_needed", columnDefinition = "CHAR(1) CHECK (is_approval_needed IN ('y', 'n'))")
    private Character isApprovalNeeded;

    /**
     * Indicates whether the room is active ('y' or 'n').
     */
    @Column(name = "is_active", columnDefinition = "CHAR(1) CHECK (is_active IN ('y', 'n'))")
    private Character isActive;

    /**
     * Indicates whether recurrence is allowed for bookings in this room ('y' or 'n').
     */
    @Column(name = "recurrence_allowed", columnDefinition = "CHAR(1) CHECK (recurrence_allowed IN ('y', 'n'))")
    private Character recurrenceAllowed;

    /**
     * The user who created this room entry.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    /**
     * The timestamp when the room was created.
     */
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    // Getters and Setters

    public Short getRoomId() {
        return roomId;
    }

    public void setRoomId(Short roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Character getExternalDisplayAvailability() {
        return externalDisplayAvailability;
    }

    public void setExternalDisplayAvailability(Character externalDisplayAvailability) {
        this.externalDisplayAvailability = externalDisplayAvailability;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public Short getCapacity() {
        return capacity;
    }

    public void setCapacity(Short capacity) {
        this.capacity = capacity;
    }

    public LocationDetails getLocationId() {
        return locationId;
    }

    public void setLocationId(LocationDetails locationId) {
        this.locationId = locationId;
    }

    public Character getBookingAllowed() {
        return bookingAllowed;
    }

    public void setBookingAllowed(Character bookingAllowed) {
        this.bookingAllowed = bookingAllowed;
    }

    public Character getIsApprovalNeeded() {
        return isApprovalNeeded;
    }

    public void setIsApprovalNeeded(Character isApprovalNeeded) {
        this.isApprovalNeeded = isApprovalNeeded;
    }

    public Character getIsActive() {
        return isActive;
    }

    public void setIsActive(Character isActive) {
        this.isActive = isActive;
    }

    public Character getRecurrenceAllowed() {
        return recurrenceAllowed;
    }

    public void setRecurrenceAllowed(Character recurrenceAllowed) {
        this.recurrenceAllowed = recurrenceAllowed;
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
}
