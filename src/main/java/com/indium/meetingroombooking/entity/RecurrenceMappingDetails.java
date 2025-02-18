package com.indium.meetingroombooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents the recurrence mapping details associated with a booking.
 */
@Entity
@Table(name = "recurrence_mapping")
public class RecurrenceMappingDetails {

    /**
     * Unique identifier for the recurrence mapping.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurrence_id")
    private Short recurrenceId;

    /**
     * The booking associated with this recurrence.
     */
    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    /**
     * The recurrence type, which can be either 'daily' or 'weekly'.
     */
    @Column(name = "recurrence_type", columnDefinition = "ENUM('daily', 'weekly')", nullable = false)
    private String recurrenceType;

    /**
     * The end date for the recurrence.
     */
    @Column(name = "recurrence_end_date")
    private LocalDateTime recurrenceEndDate;

    /**
     * The days on which the recurrence should repeat, stored as a JSON string.
     */
    @Column(name = "repeat_on_days", columnDefinition = "JSON")
    private String repeatOnDays;

    // Getters and Setters

    public Short getRecurrenceId() {
        return recurrenceId;
    }

    public void setRecurrenceId(Short recurrenceId) {
        this.recurrenceId = recurrenceId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(String recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public LocalDateTime getRecurrenceEndDate() {
        return recurrenceEndDate;
    }

    public void setRecurrenceEndDate(LocalDateTime recurrenceEndDate) {
        this.recurrenceEndDate = recurrenceEndDate;
    }

    public String getRepeatOnDays() {
        return repeatOnDays;
    }

    public void setRepeatOnDays(String repeatOnDays) {
        this.repeatOnDays = repeatOnDays;
    }
}
