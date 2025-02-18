package com.indium.meetingroombooking.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.indium.meetingroombooking.Enums.MeetingStatus;
import com.indium.meetingroombooking.Enums.MeetingType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a booking entry within the meeting room booking system.
 */
@Entity
@Table(name = "bookings")
public class Booking {

    /**
     * The unique identifier for the booking.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Short bookingId;

    /**
     * The associated location of the booking.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonProperty("location")
    @JoinColumn(name = "location_id", nullable = false)
    private LocationDetails location;

    /**
     * The room booked for the meeting.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonProperty("room")
    @JoinColumn(name = "room_id", nullable = false)
    private RoomDetails room;

    /**
     * The user who created the booking.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    /**
     * The type of meeting (e.g., internal or external).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false)
    private MeetingType meetingType;

    /**
     * The number of participants attending the meeting.
     */
    @Column(name = "participants_count", nullable = false)
    private Short participantsCount;

    /**
     * The date and time when the booking was created.
     */
    @Column(name = "booked_on", nullable = false, updatable = false)
    private LocalDateTime bookedOn;

    /**
     * The date of the meeting.
     */
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    /**
     * The starting time of the meeting.
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * The ending time of the meeting.
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * The name or title of the event.
     */
    @Column(name = "event_name", nullable = false)
    private String eventName;

    /**
     * The current status of the booking (e.g., approved, awaiting approval).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MeetingStatus status;

    /**
     * The user who approved the booking.
     */
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    /**
     * The date and time when the booking was approved.
     */
    @Column(name = "approved_on")
    private LocalDateTime approvedOn;

    /**
     * Any remarks or comments associated with the approval process.
     */
    @Column(name = "approval_remarks")
    private String approvalRemarks;

    /**
     * Indicates if the booking is recurring.
     */
    @Column(name = "is_recurring", columnDefinition = "CHAR(1) CHECK (is_recurring IN ('y', 'n')) NOT NULL DEFAULT 'n'")
    private char isRecurring;
    @Column(columnDefinition = "TEXT")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Details about the recurrence of the booking (if applicable).
     */
    @Transient
    private RecurrenceMappingDetails recurrenceDetails;

    // Getters and Setters

    public Short getBookingId() {
        return bookingId;
    }

    public void setBookingId(Short bookingId) {
        this.bookingId = bookingId;
    }

    public LocationDetails getLocation() {
        return location;
    }

    public void setLocation(LocationDetails location) {
        this.location = location;
    }

    public RoomDetails getRoom() {
        return room;
    }

    public void setRoom(RoomDetails room) {
        this.room = room;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public MeetingType getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(MeetingType meetingType) {
        this.meetingType = meetingType;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public Short getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(Short participantsCount) {
        this.participantsCount = participantsCount;
    }

    public LocalDateTime getBookedOn() {
        return bookedOn;
    }

    public void setBookedOn(LocalDateTime bookedOn) {
        this.bookedOn = bookedOn;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedOn() {
        return approvedOn;
    }

    public void setApprovedOn(LocalDateTime approvedOn) {
        this.approvedOn = approvedOn;
    }

    public String getApprovalRemarks() {
        return approvalRemarks;
    }

    public void setApprovalRemarks(String approvalRemarks) {
        this.approvalRemarks = approvalRemarks;
    }

    public char getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(char isRecurring) {
        this.isRecurring = isRecurring;
    }

    public RecurrenceMappingDetails getRecurrenceDetails() {
        return recurrenceDetails;
    }

    public void setRecurrenceDetails(RecurrenceMappingDetails recurrenceDetails) {
        this.recurrenceDetails = recurrenceDetails;
    }
}
