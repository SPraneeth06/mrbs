package com.indium.meetingroombooking.entity;

import com.indium.meetingroombooking.Enums.MeetingStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents an audit log entry capturing changes to bookings.
 */
@Entity
@Table(name = "audit_log")
public class AuditLogs {

    /**
     * The primary key for the audit log entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_log_id")
    private Short auditLogId;

    /**
     * The associated booking for this audit log.
     */
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    /**
     * The type of change (created, modified, or cancelled).
     */
    @Column(name = "change_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChangeType changeType;

    /**
     * The timestamp when the change occurred.
     */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * Comments or details about the change.
     */
    @Column(name = "comments", nullable = false, columnDefinition = "LONGTEXT")
    private String comments;

    /**
     * The user who made the change.
     */
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    /**
     * The status of the booking at the time of the change.
     */
//    @Column(name = "booking_status")
//    @Enumerated(EnumType.STRING)
//    private MeetingStatus bookingStatus;

    /**
     * Enum representing the types of changes that can be logged.
     */
    public enum ChangeType {
        CREATED, MODIFIED, CANCELLED
    }

    // Getters and Setters

    public Short getAuditLogId() {
        return auditLogId;
    }

    public void setAuditLogId(Short auditLogId) {
        this.auditLogId = auditLogId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

//    public MeetingStatus getBookingStatus() {
//        return bookingStatus;
//    }
//
//    public void setBookingStatus(MeetingStatus bookingStatus) {
//        this.bookingStatus = bookingStatus;
//    }
}
