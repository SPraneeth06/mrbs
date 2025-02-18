package com.indium.meetingroombooking.service;

import com.indium.meetingroombooking.Enums.MeetingStatus;
import com.indium.meetingroombooking.Enums.MeetingType;
import com.indium.meetingroombooking.entity.*;
import com.indium.meetingroombooking.repository.*;
import com.indium.meetingroombooking.util.TokenService;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for managing booking-related operations.
 */
@Service
public class BookingService {
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AuditLogsRepository auditLogsRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RecurrenceMappingRepository recurrenceMappingRepository;

    @Autowired
    private TokenService tokenService;

    /**
     * Creates a new booking.
     *
     * @param booking       The booking details.
     * @param authorization The authorization token.
     * @return Booking      The created booking.
     */
//    @Transactional
//    public Booking createBooking(Booking booking, String authorization) {
//        String email = tokenService.getEmailFromToken(authorization.replace("Bearer ", ""));
//        User user = userRepository.findByCompanyEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        LocationDetails location = locationRepository.findById(booking.getLocation().getLocationId())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid location ID provided"));
//
//        RoomDetails room = roomRepository.findById(booking.getRoom().getRoomId())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid room ID provided"));
//
//        booking.setUserId(user);
//        booking.setLocation(location);
//        booking.setRoom(room);
//        booking.setBookedOn(LocalDateTime.now());
//        booking.setStatus(MeetingStatus.AWAITING_APPROVAL);
//
//        Booking savedBooking = bookingRepository.save(booking);
//
//        // Log the creation of the booking
//        logAuditEntry(savedBooking, AuditLogs.ChangeType.CREATED, "Booking created", user);
//
//        if (booking.getIsRecurring() == 'y') {
//            handleRecurringBooking(booking, savedBooking);
//        }
//
//        return savedBooking;
//    }
    @Transactional
    public Booking createBooking(Short locationId, Short roomId, Booking booking, String authorization) {
        String email = tokenService.getEmailFromToken(authorization.replace("Bearer ", ""));
        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // ✅ Fetch Location and Room from Database
        LocationDetails location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid location ID provided"));

        RoomDetails room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room ID provided"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = booking.getStartTime();
        LocalDateTime endTime = booking.getEndTime();

        LocalDateTime bufferStartTime = startTime.minusMinutes(15);
        LocalDateTime bufferEndTime = endTime.plusMinutes(15);

        // ✅ Validate Buffer Time
        boolean hasConflict = bookingRepository.existsBufferTimeConflict(room, startTime, endTime, bufferStartTime, bufferEndTime);
        if (hasConflict) {
            throw new IllegalArgumentException("❌ Booking must respect a 15-minute buffer time.");
        }

        // ✅ Assign Values
        booking.setUserId(user);
        booking.setLocation(location);
        booking.setRoom(room);
        booking.setBookedOn(now);

        booking.setStatus(MeetingStatus.AWAITING_APPROVAL);

        // ✅ Save Booking
        Booking savedBooking = bookingRepository.save(booking);
        logAuditEntry(savedBooking, AuditLogs.ChangeType.CREATED, "Booking created", user);

        // ✅ 🚀 Handle Recurrence (Fix: Call `handleRecurringBooking`)
        if (booking.getIsRecurring() == 'y' && booking.getRecurrenceDetails() != null) {
            handleRecurringBooking(booking, savedBooking);
        }

        return savedBooking;
    }

    /**
     * Handles status updates for bookings.
     *
     * @param bookingId     The booking ID to be updated.
     * @param decision      The decision made (APPROVED, REJECTED, or CANCELLED).
     * @param remarks       Any additional remarks for the update.
     * @param authorization The authorization token.
     * @return String       A message indicating the result of the update.
     */
    @Transactional
    public String handleBookingStatusUpdate(Short bookingId, String decision, String remarks, String authorization) {
        String userEmail = tokenService.getEmailFromToken(authorization.replace("Bearer ", ""));
        User user = userRepository.findByCompanyEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        LocationDetails location = booking.getLocation();

        // Parse admin user IDs from JSON
        List<Short> adminUserIds = new Gson().fromJson(location.getAdminUsers(), new TypeToken<List<Short>>() {}.getType());
        boolean isAdmin = adminUserIds.contains(user.getUserId().shortValue());

        boolean isBookedByUser = booking.getUserId().getUserId().equals(user.getUserId());

        String comments;

        switch (decision.toUpperCase()) {
            case "APPROVED":
                if (!isAdmin) {
                    throw new SecurityException("❌ Only an admin can approve a booking.");
                }
                booking.setStatus(MeetingStatus.APPROVED);
                booking.setApprovedBy(user);
                booking.setApprovedOn(LocalDateTime.now());
                booking.setApprovalRemarks(remarks != null ? remarks : "Approved by admin");
                comments = "Booking approved by Admin: " + user.getCompanyEmail();
                break;

            case "REJECTED":
                if (!isAdmin) {
                    throw new SecurityException("❌ Only an admin can reject a booking.");
                }
                booking.setStatus(MeetingStatus.REJECTED);
                booking.setApprovalRemarks(remarks != null ? remarks : "Rejected by admin");
                booking.setApprovedOn(LocalDateTime.now());
                comments = "Booking rejected by Admin: " + user.getCompanyEmail();
                break;

            case "CANCELLED":
                if (!isBookedByUser) {
                    throw new SecurityException("❌ Only the user who booked can cancel.");
                }
                booking.setStatus(MeetingStatus.CANCELLED);
                booking.setApprovalRemarks("Cancelled by user");
                booking.setApprovedOn(null);
                comments = "Booking cancelled by User: " + user.getCompanyEmail();
                break;

            default:
                throw new IllegalArgumentException("❌ Invalid decision. Use 'APPROVED', 'REJECTED', or 'CANCELLED'.");
        }

        bookingRepository.save(booking);
        logAuditEntry(booking, AuditLogs.ChangeType.MODIFIED, comments, user);

        return "✅ Booking " + decision.toLowerCase() + "d successfully.";
    }


    /**
     * Updates an existing booking.
     *
     * @param bookingId     The booking ID to be updated.
     * @param updates       The updates to be applied.
     * @param authorization The authorization token.
     * @return Booking      The updated booking.
     */
    @Transactional
    public Booking updateBooking(Short bookingId, Map<String, Object> updates, String authorization) {
        String email = tokenService.getEmailFromToken(authorization.replace("Bearer ", ""));
        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        validateUserOwnership(existingBooking, email);

        boolean statusResetRequired = false;
        StringBuilder changeComments = new StringBuilder("Updated fields: ");

        // Store old values for comparison
        LocalDateTime oldStartTime = existingBooking.getStartTime();
        LocalDateTime oldEndTime = existingBooking.getEndTime();
        LocalDate oldEventDate = existingBooking.getEventDate();
        char oldIsRecurring = existingBooking.getIsRecurring();

        // **Check if startTime or endTime is being modified**
        if (updates.containsKey("startTime") || updates.containsKey("endTime")) {
            LocalDateTime newStartTime = updates.containsKey("startTime")
                    ? LocalDateTime.parse((String) updates.get("startTime"))
                    : oldStartTime;

            LocalDateTime newEndTime = updates.containsKey("endTime")
                    ? LocalDateTime.parse((String) updates.get("endTime"))
                    : oldEndTime;

            LocalDateTime bufferStartTime = newStartTime.minusMinutes(15);
            LocalDateTime bufferEndTime = newEndTime.plusMinutes(15);

            boolean hasConflict = bookingRepository.existsBufferTimeConflict(
                    existingBooking.getRoom(), newStartTime, newEndTime, bufferStartTime, bufferEndTime);

            if (hasConflict) {
                throw new IllegalArgumentException("❌ Booking must respect a 15-minute buffer time.");
            }

            existingBooking.setStartTime(newStartTime);
            existingBooking.setEndTime(newEndTime);
            changeComments.append("Start time, End time updated. ");
            statusResetRequired = true;  // ✅ Set status to AWAITING_APPROVAL
        }

        // **Check if eventDate is modified**
        if (updates.containsKey("eventDate")) {
            LocalDate newEventDate = LocalDate.parse((String) updates.get("eventDate"));
            existingBooking.setEventDate(newEventDate);
            changeComments.append("Event date updated. ");
            statusResetRequired = true;  // ✅ Set status to AWAITING_APPROVAL
        }

        // **Check if isRecurring is modified**
        if (updates.containsKey("isRecurring")) {
            char newIsRecurring = ((String) updates.get("isRecurring")).charAt(0);
            existingBooking.setIsRecurring(newIsRecurring);
            changeComments.append("Recurrence status updated. ");
            statusResetRequired = true;  // ✅ Set status to AWAITING_APPROVAL
        }
        if (updates.containsKey("description")) {
            existingBooking.setDescription((String) updates.get("description"));
            changeComments.append("Description updated. ");
        }

        // **Apply all other updates** (Non-status affecting)
        applyUpdatesToBooking(existingBooking, updates, changeComments);

        // **Set status to AWAITING_APPROVAL only if necessary**
        if (statusResetRequired) {
            existingBooking.setStatus(MeetingStatus.AWAITING_APPROVAL);
        }

        Booking updatedBooking = bookingRepository.save(existingBooking);
        logAuditEntry(updatedBooking, AuditLogs.ChangeType.MODIFIED, changeComments.toString(), user);

        return updatedBooking;
    }


    public void deleteBooking(Short bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    public List<Booking> getAllBookings() {
        return StreamSupport.stream(bookingRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByUser(Short userId) {
        User user = findUserById(userId);
        return bookingRepository.findByUserId(user);
    }

    public List<Booking> getBookingsByRoom(Short roomId) {
        RoomDetails room = findRoomById(roomId);
        return bookingRepository.findByRoom(room);
    }

    public List<Booking> getBookingsByLocation(Short locationId) {
        LocationDetails location = findLocationById(locationId);
        return bookingRepository.findByLocation(location);
    }

    private void handleRecurringBooking(Booking booking, Booking savedBooking) {
        if (booking.getRecurrenceDetails() == null) {
            throw new IllegalArgumentException("❌ Recurrence details must be provided for recurring bookings.");
        }

        RecurrenceMappingDetails recurrenceMapping = new RecurrenceMappingDetails();
        recurrenceMapping.setBooking(savedBooking);
        recurrenceMapping.setRecurrenceType(booking.getRecurrenceDetails().getRecurrenceType());
        recurrenceMapping.setRecurrenceEndDate(booking.getRecurrenceDetails().getRecurrenceEndDate());
        recurrenceMapping.setRepeatOnDays(booking.getRecurrenceDetails().getRepeatOnDays());

        // ✅ Ensure data is saved
        recurrenceMappingRepository.save(recurrenceMapping);
    }


    public boolean checkIfUserIsAdmin(Long userId, LocationDetails location) {
        if (location.getAdminUsers() == null || location.getAdminUsers().isEmpty()) {
            return false; // No admins assigned
        }

        // Convert adminUsers JSON field to a List of Short
        List<Short> adminUserIds = new Gson().fromJson(location.getAdminUsers(), new TypeToken<List<Short>>() {}.getType());

        // Check if the given userId exists in the list
        return adminUserIds.contains(userId);
    }


    private void validateUserOwnership(Booking booking, String email) {
        if (!booking.getUserId().getCompanyEmail().equals(email)) {
            throw new SecurityException("You do not have permission to update this booking");
        }
    }

    private void applyUpdatesToBooking(Booking existingBooking, Map<String, Object> updates, StringBuilder changeComments) {
        if (updates.containsKey("eventName")) {
            existingBooking.setEventName(updates.get("eventName").toString());
            changeComments.append("eventName, ");
        }

        if (updates.containsKey("meetingType")) {
            existingBooking.setMeetingType(MeetingType.valueOf(updates.get("meetingType").toString().toUpperCase()));
            changeComments.append("meetingType, ");
        }

        if (updates.containsKey("participantsCount")) {
            existingBooking.setParticipantsCount(Short.valueOf(updates.get("participantsCount").toString()));
            changeComments.append("participantsCount, ");
        }

        if (updates.containsKey("eventDate")) {
            existingBooking.setEventDate(LocalDate.parse(updates.get("eventDate").toString()));
            changeComments.append("eventDate, ");
        }

        if (updates.containsKey("startTime")) {
            existingBooking.setStartTime(LocalDateTime.parse(updates.get("startTime").toString()));
            changeComments.append("startTime, ");
        }

        if (updates.containsKey("endTime")) {
            existingBooking.setEndTime(LocalDateTime.parse(updates.get("endTime").toString()));
            changeComments.append("endTime, ");
        }

        if (updates.containsKey("location")) {
            Short locationId = Short.valueOf(((Map<String, Object>) updates.get("location")).get("locationId").toString());
            LocationDetails location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid location ID provided"));
            existingBooking.setLocation(location);
            changeComments.append("location, ");
        }

        if (updates.containsKey("room")) {
            Short roomId = Short.valueOf(((Map<String, Object>) updates.get("room")).get("roomId").toString());
            RoomDetails room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid room ID provided"));
            existingBooking.setRoom(room);
            changeComments.append("room, ");
        }
    }

    private boolean resetApprovalFieldsIfNecessary(Booking existingBooking, boolean statusResetRequired, StringBuilder changeComments) {
        if (statusResetRequired && existingBooking.getStatus() == MeetingStatus.APPROVED) {
            existingBooking.setStatus(MeetingStatus.AWAITING_APPROVAL);
            existingBooking.setApprovedBy(null);
            existingBooking.setApprovedOn(null);
            existingBooking.setApprovalRemarks(null);
            changeComments.append("Status reset to AWAITING_APPROVAL.");
        }
        return statusResetRequired;
    }

    private void logAuditEntry(Booking booking, AuditLogs.ChangeType changeType, String comments, User createdBy) {
        AuditLogs auditLog = new AuditLogs();
        auditLog.setBooking(booking);
        auditLog.setChangeType(changeType);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setComments(comments);  // 📌 Stores a detailed description of the change
        auditLog.setCreatedBy(createdBy);

        auditLogsRepository.save(auditLog);
    }

    private User findUserById(Short userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private RoomDetails findRoomById(Short roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
    }

    private LocationDetails findLocationById(Short locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));
    }
}
