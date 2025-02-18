package com.indium.meetingroombooking.controller;

import com.indium.meetingroombooking.Enums.MeetingType;
import com.indium.meetingroombooking.entity.Booking;
import com.indium.meetingroombooking.entity.LocationDetails;
import com.indium.meetingroombooking.entity.RecurrenceMappingDetails;
import com.indium.meetingroombooking.entity.RoomDetails;
import com.indium.meetingroombooking.repository.ConfigMasterRepository;
import com.indium.meetingroombooking.repository.UserRepository;
import com.indium.meetingroombooking.service.BookingService;
import com.indium.meetingroombooking.service.LocationService;
import com.indium.meetingroombooking.service.RoomService;
import com.indium.meetingroombooking.util.TokenService;
import com.nimbusds.jose.shaded.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for handling location, room, and booking-related operations.
 */
@RestController
@RequestMapping("/api")
public class ApplicationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ConfigMasterRepository configMasterRepository;

    /**
     * Adds a new location.
     *
     * @param payload        The location details.
     * @param authorization  The authorization token.
     * @return ResponseEntity with success or failure message.
     */
//    @PostMapping("/addlocations")
//    public ResponseEntity<String> addLocation(@RequestBody Map<String, Object> payload,
//                                              @RequestHeader("Authorization") String authorization) {
//        try {
//            LocationDetails locationDetails = new LocationDetails();
//            locationDetails.setLocationName((String) payload.get("locationName"));
//            locationDetails.setShortLocationName((String) payload.get("shortLocationName"));
//            locationDetails.setIsActive(((String) payload.get("isActive")).charAt(0));
//            locationDetails.setAdminUsers(new Gson().toJson(payload.get("adminUsers")));
//
//            LocationDetails createdLocation = locationService.addLocation(locationDetails, authorization);
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body("✅ Location added successfully.\nLocation Name: " + createdLocation.getLocationName() +
//                            "\nLocation ID: " + createdLocation.getLocationId());
//        } catch (SecurityException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ Authorization failed: " + e.getMessage());
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid input: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("❌ An unexpected error occurred: " + e.getMessage());
//        }
//    }

    @PostMapping("/addlocations")
    public ResponseEntity<?> addLocation(@RequestBody Map<String, Object> payload,
                                         @RequestHeader("Authorization") String authorization) {
        try {
            LocationDetails locationDetails = new LocationDetails();
            locationDetails.setLocationName((String) payload.get("locationName"));
            locationDetails.setShortLocationName((String) payload.get("shortLocationName"));
            locationDetails.setIsActive(((String) payload.get("isActive")).charAt(0));

            // Extract and validate numeric values
            locationDetails.setBookingAllowedWindowInDays(
                    payload.get("bookingAllowedWindowInDays") instanceof Number
                            ? ((Number) payload.get("bookingAllowedWindowInDays")).shortValue()
                            : Short.valueOf(payload.get("bookingAllowedWindowInDays").toString())
            );

            locationDetails.setNoticeDurationToBookInMin(
                    payload.get("noticeDurationToBookInMin") instanceof Number
                            ? ((Number) payload.get("noticeDurationToBookInMin")).byteValue()
                            : Byte.valueOf(payload.get("noticeDurationToBookInMin").toString())
            );

            locationDetails.setRecurrenceCountAllowed(
                    payload.get("recurrenceCountAllowed") instanceof Number
                            ? ((Number) payload.get("recurrenceCountAllowed")).byteValue()
                            : Byte.valueOf(payload.get("recurrenceCountAllowed").toString())
            );

            // Extract and validate adminUserIds
            List<Short> adminUserIds = (payload.get("adminUserIds") instanceof List<?>)
                    ? ((List<?>) payload.get("adminUserIds")).stream()
                    .map(id -> {
                        if (id instanceof Number) {
                            return ((Number) id).shortValue();
                        } else {
                            return Short.valueOf(id.toString());
                        }
                    })
                    .toList()
                    : List.of();

            LocationDetails createdLocation = locationService.addLocation(locationDetails, authorization, adminUserIds);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("✅ Location added successfully.\nLocation Name: " + createdLocation.getLocationName() +
                            "\nLocation ID: " + createdLocation.getLocationId());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ Authorization failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ An unexpected error occurred: " + e.getMessage());
        }
    }


    /**
     * Updates an existing location.
     *
     * @param locationId     The location ID.
     * @param updates        The updates to be applied.
     * @param authorization  The authorization token.
     * @return ResponseEntity with success or failure message.
     */
    @PatchMapping("/editLocation/{locationId}")
    public ResponseEntity<String> editLocation(@PathVariable Short locationId,
                                               @RequestBody Map<String, Object> updates,
                                               @RequestHeader("Authorization") String authorization) {
        try {
            LocationDetails updatedLocation = locationService.editLocation(locationId, updates, authorization);
            return ResponseEntity.ok("✅ Location updated successfully.\nUpdated Location Name: " +
                    updatedLocation.getLocationName());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ Authorization failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ An unexpected error occurred: " + e.getMessage());
        }
    }


    /**
     * Deletes a location.
     *
     * @param locationId     The location ID to be deleted.
     * @param authorization  The authorization token.
     * @return ResponseEntity with success or failure message.
     */
    @DeleteMapping("/deleteLocation/{locationId}")
    public ResponseEntity<String> deleteLocation(@PathVariable Short locationId,
                                                 @RequestHeader("Authorization") String authorization) {
        try {
            String result = locationService.deleteLocation(locationId, authorization);
            return ResponseEntity.ok("✅ " + result);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ Authorization failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Adds a new room.
     *
     * @param payload        The room details.
     * @param authorization  The authorization token.
     * @return ResponseEntity with success or failure message.
     */
    @PostMapping("/addroom")
    public ResponseEntity<String> addRoom(@RequestBody Map<String, Object> payload,
                                          @RequestHeader("Authorization") String authorization) {
        try {
            RoomDetails room = roomService.addRoom(payload, authorization);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("✅ Room added successfully.\nRoom Name: " + room.getRoomName() +
                            "\nRoom ID: " + room.getRoomId());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ Authorization failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Updates an existing room.
     *
     * @param roomId         The room ID.
     * @param payload        The updates to be applied.
     * @param authorization  The authorization token.
     * @return ResponseEntity with success or failure message.
     */
    @PatchMapping("/editroom/{roomId}")
    public ResponseEntity<String> editRoom(@PathVariable Short roomId,
                                           @RequestBody Map<String, Object> payload,
                                           @RequestHeader("Authorization") String authorization) {
        try {
            RoomDetails updatedRoom = roomService.editRoom(roomId, payload, authorization);
            return ResponseEntity.ok("✅ Room updated successfully.\nRoom Name: " + updatedRoom.getRoomName() +
                    "\nCapacity: " + updatedRoom.getCapacity() +
                    "\nFacilities: " + updatedRoom.getFacilities());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ Authorization failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Deletes an existing room.
     *
     * @param roomId         The room ID to be deleted.
     * @param authorization  The authorization token.
     * @return ResponseEntity with success or failure message.
     */
    @DeleteMapping("/deleteroom/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Short roomId,
                                             @RequestHeader("Authorization") String authorization) {
        try {
            String responseMessage = roomService.deleteRoom(roomId, authorization);
            return ResponseEntity.ok("✅ Room deleted successfully.\n" + responseMessage);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ Authorization failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Creates a new booking.
     *
     * @param payload        The booking details.
     * @param authorization  The authorization token.
     * @return ResponseEntity with success or failure message.
     */
    @PostMapping("/booking")
    public ResponseEntity<String> createBooking(@RequestBody Map<String, Object> payload,
                                                @RequestHeader("Authorization") String authorization) {
        try {
            // ✅ Extract locationId and roomId
            Short locationId = ((Number) payload.get("locationId")).shortValue();
            Short roomId = ((Number) payload.get("roomId")).shortValue();

            // ✅ Create Booking Object
            Booking booking = new Booking();
            booking.setMeetingType(MeetingType.valueOf((String) payload.get("meetingType")));
            booking.setParticipantsCount(((Number) payload.get("participantsCount")).shortValue());
            booking.setEventDate(LocalDate.parse((String) payload.get("eventDate")));
            booking.setStartTime(LocalDateTime.parse((String) payload.get("startTime")));
            booking.setEndTime(LocalDateTime.parse((String) payload.get("endTime")));
            booking.setEventName((String) payload.get("eventName"));
            booking.setIsRecurring(((String) payload.get("isRecurring")).charAt(0));

            // 🚀 Handle Recurring Booking Details
            if (booking.getIsRecurring() == 'y') {
                RecurrenceMappingDetails recurrence = new RecurrenceMappingDetails();
                recurrence.setRecurrenceType((String) payload.get("recurrenceType"));
                recurrence.setRecurrenceEndDate(LocalDateTime.parse((String) payload.get("recurrenceEndDate")));
                recurrence.setRepeatOnDays((String) payload.get("repeatOnDays"));

                booking.setRecurrenceDetails(recurrence);
            }

            // ✅ Call Service Layer
            Booking createdBooking = bookingService.createBooking(locationId, roomId, booking, authorization);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("✅ Booking created successfully.\nBooking ID: " + createdBooking.getBookingId() +
                            "\nStatus: " + createdBooking.getStatus());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Booking creation failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Updates an existing booking.
     *
     * @param bookingId      The booking ID.
     * @param updates        The updates to be applied.
     * @param authorization  The authorization token.
     * @return ResponseEntity with success or failure message.
     */
    @PatchMapping("/updateBooking/{bookingId}")
    public ResponseEntity<String> updateBooking(@PathVariable Short bookingId,
                                                @RequestBody Map<String, Object> updates,
                                                @RequestHeader("Authorization") String authorization) {
        try {
            Booking updatedBooking = bookingService.updateBooking(bookingId, updates, authorization);
            return ResponseEntity.ok("✅ Booking updated successfully.\nUpdated Booking ID: " + updatedBooking.getBookingId() +
                    "\nUpdated Status: " + updatedBooking.getStatus());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid input: " + e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ Authorization failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ An unexpected error occurred: " + e.getMessage());
        }
    }
    @PatchMapping("admin/updateBookingStatus/{bookingId}")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Short bookingId,
                                                 @RequestBody Map<String, Object> requestBody,
                                                 @RequestHeader("Authorization") String authorization) {
        try {
            String decision = (String) requestBody.get("decision");
            String remarks = (String) requestBody.getOrDefault("remarks", "");

            if (decision == null || decision.isEmpty()) {
                throw new IllegalArgumentException("Decision field is required.");
            }

            String result = bookingService.handleBookingStatusUpdate(bookingId, decision, remarks, authorization);
            return ResponseEntity.ok("✅ Booking status updated successfully.\n" + result);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ Authorization failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ An unexpected error occurred");
        }
    }
}
