package com.indium.meetingroombooking.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the various statuses that a meeting or booking can have.
 */
public enum MeetingStatus {

    /** Status indicating that the booking is awaiting approval. */
    AWAITING_APPROVAL("AWAITING_APPROVAL"),

    /** Status indicating that the booking has been approved. */
    APPROVED("APPROVED"),

    /** Status indicating that the booking has been cancelled. */
    CANCELLED("CANCELLED"),

    /** Status indicating that the booking has been rejected. */
    REJECTED("REJECTED");

    /** The string value associated with the meeting status. */
    private final String value;

    /**
     * Constructor for the MeetingStatus enum.
     *
     * @param value The string representation of the meeting status.
     */
    MeetingStatus(String value) {
        this.value = value;
    }

    /**
     * Retrieves the string representation of the meeting status.
     *
     * @return The string representation of the meeting status.
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Converts a string value to the corresponding MeetingStatus enum.
     *
     * @param value The string representation of the status.
     * @return The corresponding MeetingStatus enum.
     * @throws IllegalArgumentException if the value does not match any status.
     */
    @JsonCreator
    public static MeetingStatus fromValue(String value) {
        for (MeetingStatus status : MeetingStatus.values()) {
            if (status.name().equalsIgnoreCase(value) || status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}
