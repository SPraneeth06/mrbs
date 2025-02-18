package com.indium.meetingroombooking.service;

import com.indium.meetingroombooking.entity.LocationDetails;
import com.indium.meetingroombooking.entity.RoomDetails;
import com.indium.meetingroombooking.entity.User;
import com.indium.meetingroombooking.repository.LocationRepository;
import com.indium.meetingroombooking.repository.RoomRepository;
import com.indium.meetingroombooking.repository.UserRepository;
import com.indium.meetingroombooking.util.TokenService;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service class for handling room-related operations.
 */
@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    /**
     * Adds a new room for the specified location.
     *
     * @param payload       Request payload containing room details.
     * @param authorization Authorization token for verifying user.
     * @return RoomDetails  The created room details.
     */
//    public RoomDetails addRoom(Map<String, Object> payload, String authorization) {
//        String email = extractEmailFromToken(authorization);
//        log("🔍 Email extracted for adding room: " + email);
//
//        LocationDetails locationDetails = getLocationForAdmin(email);
//        User user = userRepository.findByCompanyEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        if (locationDetails == null) {
//            log("❌ Admin access required for adding rooms at the location.");
//            throw new SecurityException("Admin access required for this location");
//        }
//
//        // Check if room name already exists for this location
//        String roomName = (String) payload.get("roomName");
//        boolean roomExists = roomRepository.existsByRoomNameAndLocationId(roomName, locationDetails.getLocationId());
//
//        if (roomExists) {
//            log("❌ Room with name '" + roomName + "' already exists in location: " + locationDetails.getLocationName());
//            throw new IllegalArgumentException("Room with name '" + roomName + "' already exists in this location.");
//        }
//
//        RoomDetails roomDetails = new RoomDetails();
//        roomDetails.setCreatedBy(user);
//        roomDetails.setRoomName(roomName);
//        roomDetails.setExternalDisplayAvailability(((String) payload.get("externalDisplayAvailability")).charAt(0));
//        roomDetails.setFacilities(new Gson().toJson(payload.get("facilities")));  // Store facilities as JSON
//        roomDetails.setCapacity(Short.valueOf(payload.get("capacity").toString()));
//        roomDetails.setIsActive('y');
//        roomDetails.setBookingAllowed('y');
//        roomDetails.setIsApprovalNeeded('y');
//        roomDetails.setRecurrenceAllowed('y');
//        roomDetails.setCreatedOn(LocalDateTime.now());
//        roomDetails.setLocationId(locationDetails);
//
//        log("✅ Room created successfully by admin: " + email);
//        return roomRepository.save(roomDetails);
//    }
    public RoomDetails addRoom(Map<String, Object> payload, String authorization) {
        String email = extractEmailFromToken(authorization);
        log("🔍 Email extracted for adding room: " + email);

        // Fetch user from repository using email
        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch all locations where this user is an admin
        List<LocationDetails> locations = (List<LocationDetails>) locationRepository.findAll();

        LocationDetails adminLocation = locations.stream()
                .filter(location -> {
                    // Convert JSON adminUsers to List<Short>
                    List<Short> adminUserIds = new Gson().fromJson(location.getAdminUsers(), new TypeToken<List<Short>>() {}.getType());
                    return adminUserIds.contains(user.getUserId().shortValue());
                })
                .findFirst()
                .orElseThrow(() -> new SecurityException("No admin privileges found for user: " + email));

        log("✅ Admin privileges verified for location: " + adminLocation.getLocationName());

        // Check if room name already exists for this location
        String roomName = (String) payload.get("roomName");
        boolean roomExists = roomRepository.existsByRoomNameAndLocationId(roomName, adminLocation.getLocationId());

        if (roomExists) {
            log("❌ Room with name '" + roomName + "' already exists in location: " + adminLocation.getLocationName());
            throw new IllegalArgumentException("Room with name '" + roomName + "' already exists in this location.");
        }

        // Convert amenities list to JSON
        List<String> facilitiesList = (List<String>) payload.get("facilities");
        String facilitiesJson = new Gson().toJson(facilitiesList);

        // Extract boolean-like values from payload (y/n)
        char isActive = ((String) payload.get("isActive")).toLowerCase().charAt(0);
        char bookingAllowed = ((String) payload.get("bookingAllowed")).toLowerCase().charAt(0);
        char isApprovalNeeded = ((String) payload.get("isApprovalNeeded")).toLowerCase().charAt(0);
        char recurrenceAllowed = ((String) payload.get("recurrenceAllowed")).toLowerCase().charAt(0);

        // Create new room entity
        RoomDetails roomDetails = new RoomDetails();
        roomDetails.setCreatedBy(user);
        roomDetails.setRoomName(roomName);
        roomDetails.setExternalDisplayAvailability(((String) payload.get("externalDisplayAvailability")).charAt(0));
        roomDetails.setFacilities(facilitiesJson);  // Store facilities as JSON
        roomDetails.setCapacity(Short.valueOf(payload.get("capacity").toString()));
        roomDetails.setIsActive(isActive);
        roomDetails.setBookingAllowed(bookingAllowed);
        roomDetails.setIsApprovalNeeded(isApprovalNeeded);
        roomDetails.setRecurrenceAllowed(recurrenceAllowed);
        roomDetails.setCreatedOn(LocalDateTime.now());
        roomDetails.setLocationId(adminLocation);  // Automatically assigned location

        log("✅ Room created successfully by admin: " + email);
        return roomRepository.save(roomDetails);
    }

    /**
     * Edits the details of an existing room.
     *
     * @param roomId        Room ID to be updated.
     * @param payload       Request payload containing updated room details.
     * @param authorization Authorization token for verifying user.
     * @return RoomDetails  The updated room details.
     */
    @Transactional
    public RoomDetails editRoom(Short roomId, Map<String, Object> payload, String authorization) {
        String email = extractEmailFromToken(authorization);
        log("🔍 Email extracted for editing room: " + email);

        // Fetch existing room
        RoomDetails existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Validate that the user has admin access for the location
        validateAdminAccess(email, existingRoom.getLocationId());

        StringBuilder changeLog = new StringBuilder("Updated fields: ");

        // Update the room details based on the provided payload
        if (payload.containsKey("roomName")) {
            existingRoom.setRoomName((String) payload.get("roomName"));
            changeLog.append("roomName, ");
        }
        if (payload.containsKey("externalDisplayAvailability")) {
            existingRoom.setExternalDisplayAvailability(((String) payload.get("externalDisplayAvailability")).charAt(0));
            changeLog.append("externalDisplayAvailability, ");
        }
        if (payload.containsKey("facilities")) {
            existingRoom.setFacilities(new Gson().toJson(payload.get("facilities")));
            changeLog.append("facilities, ");
        }
        if (payload.containsKey("capacity")) {
            existingRoom.setCapacity(Short.valueOf(payload.get("capacity").toString()));
            changeLog.append("capacity, ");
        }

        log("✅ Room updated successfully for Room ID: " + roomId + " by admin: " + email);
        log("📝 " + changeLog.toString());

        return roomRepository.save(existingRoom);
    }

    /**
     * Deletes a room.
     *
     * @param roomId        Room ID to be deleted.
     * @param authorization Authorization token for verifying user.
     * @return String       Success message after deletion.
     */
    public String deleteRoom(Short roomId, String authorization) {
        String email = extractEmailFromToken(authorization);
        log("🔍 Email extracted for deleting room: " + email);

        RoomDetails existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        validateAdminAccess(email, existingRoom.getLocationId());

        roomRepository.delete(existingRoom);
        log("✅ Room deleted successfully for ID: " + roomId + " by admin: " + email);
        return "Room deleted successfully.";
    }

    /**
     * Extracts email from authorization token.
     *
     * @param authorization Authorization token containing user details.
     * @return String       Extracted email.
     */
    private String extractEmailFromToken(String authorization) {
        String token = authorization.replace("Bearer ", "");
        String email = tokenService.getEmailFromToken(token);
        if (email == null) {
            throw new SecurityException("Invalid token or email not found in token");
        }
        return email;
    }

    /**
     * Fetches the location details for which the user has admin access.
     *
     * @param email Admin user's email.
     * @return LocationDetails Location details where the user is an admin.
     */
    private LocationDetails getLocationForAdmin(String email) {
        List<LocationDetails> locations = (List<LocationDetails>) locationRepository.findAll();
        log("Fetched locations from DB: " + locations.size());

        return locations.stream()
                .filter(location -> {
                    log("Checking location: " + location.getLocationName());
                    List<Map<String, String>> adminUsers = parseAdminUsers(location.getAdminUsers());
                    log("Parsed Admin Users: " + adminUsers);

                    return adminUsers.stream()
                            .anyMatch(admin -> {
                                boolean match = admin.get("email").equalsIgnoreCase(email);
                                log("Comparing with admin email: " + admin.get("email") + " == " + email + " -> " + match);
                                return match;
                            });
                })
                .findFirst()
                .orElseThrow(() -> {
                    log("❌ No admin privileges found for user: " + email);
                    return new SecurityException("No admin privileges found for this user");
                });
    }

    /**
     * Validates if the given user has admin access to the specified location.
     *
     * @param email          User's email to be validated.
     * @param locationDetails Location details to check against.
     */
    private void validateAdminAccess(String email, LocationDetails locationDetails) {
        List<Map<String, String>> adminUsers = parseAdminUsers(locationDetails.getAdminUsers());
        boolean isAdmin = adminUsers.stream()
                .anyMatch(admin -> admin.get("email").equalsIgnoreCase(email));

        if (!isAdmin) {
            throw new SecurityException("Only admins can perform this operation for this location");
        }
    }

    /**
     * Parses the admin users JSON string into a list of key-value pairs.
     *
     * @param adminUsersJson JSON string representing admin users.
     * @return List of key-value pairs representing admin users.
     */
    private List<Map<String, String>> parseAdminUsers(String adminUsersJson) {
        try {
            if (adminUsersJson == null || adminUsersJson.isEmpty()) {
                log("❌ Empty adminUsers JSON");
                return List.of();
            }

            // Handle possible double stringification issue
            if (adminUsersJson.startsWith("\"")) {
                adminUsersJson = adminUsersJson.substring(1, adminUsersJson.length() - 1);
            }
            adminUsersJson = adminUsersJson.replace("\\", ""); // Remove escape characters

            List<Map<String, String>> adminUsers = new Gson().fromJson(adminUsersJson, new TypeToken<List<Map<String, String>>>() {}.getType());
            log("✅ Successfully parsed Admin Users: " + adminUsers);
            return adminUsers;
        } catch (Exception e) {
            log("❌ Error parsing adminUsers JSON: " + adminUsersJson);
            throw new RuntimeException("Error parsing adminUsers JSON: " + e.getMessage());
        }
    }

    /**
     * Utility method to print logs.
     *
     * @param message The message to log.
     */
    private void log(String message) {
        System.out.println(message);
    }
}
