/**
 * LocationService
 *
 * This service handles operations related to locations such as adding, editing,
 * and deleting location details. It also enforces access control and ensures
 * that location names do not duplicate.
 *
 * @author YourName
 * @version 1.0
 */
package com.indium.meetingroombooking.service;

import com.indium.meetingroombooking.entity.ConfigMaster;
import com.indium.meetingroombooking.entity.LocationDetails;
import com.indium.meetingroombooking.entity.User;
import com.indium.meetingroombooking.repository.ConfigMasterRepository;
import com.indium.meetingroombooking.repository.LocationRepository;
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
import java.util.stream.Collectors;

/**
 * Service for managing location operations including adding, editing, and deleting locations.
 */
@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ConfigMasterRepository configMasterRepository;

    /**
     * Adds a new location after verifying the user's authorization.
     *
     * @param locationDetails The details of the location to be added.
     * @param authorization   The authorization token of the user.
     * @return The saved location details.
     */
//    @Transactional
//    public LocationDetails addLocation(LocationDetails locationDetails, String authorization) {
//        String email = tokenService.getEmailFromToken(authorization.replace("Bearer ", ""));
//        System.out.println("🔍 Email extracted from token: " + email);
//
//        List<String> authorizedEmails = configMasterRepository.getEmailsFromConfigMaster();
//        System.out.println("🔍 Authorized emails from config_master: " + authorizedEmails);
//
//        boolean isAuthorized = authorizedEmails.stream().anyMatch(email::equals);
//        if (!isAuthorized) {
//            System.out.println("❌ Authorization failed. No match found for email: " + email);
//            throw new SecurityException("You are not authorized to add a location");
//        }
//
//        System.out.println("✅ Authorization successful. Email is authorized to add a location.");
//
//        if (locationRepository.existsByLocationNameIgnoreCase(locationDetails.getLocationName())) {
//            System.out.println("❌ Duplicate location name: " + locationDetails.getLocationName());
//            throw new IllegalArgumentException("A location with the name '" + locationDetails.getLocationName() + "' already exists.");
//        }
//
//        User user = userRepository.findByCompanyEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        locationDetails.setCreatedBy(user);
//        locationDetails.setCreatedOn(LocalDateTime.now());
//        locationDetails.setIsActive(Character.toLowerCase(locationDetails.getIsActive()) == 'y' ? 'y' : 'n');
//        locationDetails.setBookingAllowedWindowInDays((short) 30);
//        locationDetails.setNoticeDurationToBookInMin((byte) 15);
//        locationDetails.setRecurrenceCountAllowed((byte) 3);
//
//        if (locationDetails.getAdminUsers() != null) {
//            locationDetails.setAdminUsers(new Gson().toJson(parseAdminUsers(locationDetails.getAdminUsers())));
//        }
//
//        System.out.println("✅ Location details saved successfully for email: " + email);
//        return locationRepository.save(locationDetails);
//    }
    @Transactional
    public LocationDetails addLocation(LocationDetails locationDetails, String authorization, List<Short> adminUserIds) {
        // Extract email from token
        String email = tokenService.getEmailFromToken(authorization.replace("Bearer ", ""));
        System.out.println("🔍 Email extracted from token: " + email);

        // Retrieve authorized user IDs from config_master
        List<String> authorizedUserIds = configMasterRepository.getUserIdsFromConfigMaster(); // Fetching user IDs

        // Fetch user by email
        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if the extracted user ID is authorized
        if (!authorizedUserIds.contains(String.valueOf(user.getUserId()))) {
            System.out.println("❌ Authorization failed. User ID not found in config_master: " + user.getUserId());
            throw new SecurityException("You are not authorized to add a location");
        }

        System.out.println("✅ Authorization successful. User is authorized to add a location.");

        // Check for duplicate location name
        if (locationRepository.existsByLocationNameIgnoreCase(locationDetails.getLocationName())) {
            System.out.println("❌ Duplicate location name: " + locationDetails.getLocationName());
            throw new IllegalArgumentException("A location with the name '" + locationDetails.getLocationName() + "' already exists.");
        }

        // Fetch admin users from provided user IDs
        List<User> adminUsersList = (List<User>) userRepository.findAllById(adminUserIds);
        if (adminUsersList.isEmpty()) {
            throw new IllegalArgumentException("No valid admin users found for the provided IDs.");
        }

        // Convert admin users to a JSON array containing only user IDs
        String adminUsersJson = new Gson().toJson(adminUsersList.stream()
                .map(User::getUserId)  // Extract only userId
                .toList());

        // Set location details
        locationDetails.setCreatedBy(user);
        locationDetails.setCreatedOn(LocalDateTime.now());
        locationDetails.setIsActive(Character.toLowerCase(locationDetails.getIsActive()) == 'y' ? 'y' : 'n');
        locationDetails.setAdminUsers(adminUsersJson); // Store only user IDs

        // Extract numeric values from request payload safely
        locationDetails.setBookingAllowedWindowInDays(locationDetails.getBookingAllowedWindowInDays());
        locationDetails.setNoticeDurationToBookInMin(locationDetails.getNoticeDurationToBookInMin());
        locationDetails.setRecurrenceCountAllowed(locationDetails.getRecurrenceCountAllowed());

        System.out.println("✅ Location details saved successfully for user ID: " + user.getUserId());
        return locationRepository.save(locationDetails);
    }


    /**
     * Edits an existing location based on the provided updates.
     *
     * @param locationId  The ID of the location to be edited.
     * @param updates     The updates to be applied.
     * @param authorization The authorization token of the user.
     * @return The updated location details.
     */
    @Transactional
    public LocationDetails editLocation(Short locationId, Map<String, Object> updates, String authorization) {
        // Extract email from token
        String email = tokenService.getEmailFromToken(authorization.replace("Bearer ", ""));
        System.out.println("🔍 Email extracted from token: " + email);

        // Retrieve authorized user IDs from config_master
        List<String> authorizedUserIds = configMasterRepository.getUserIdsFromConfigMaster();

        // Fetch user by email
        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if the extracted user ID is authorized
        if (!authorizedUserIds.contains(String.valueOf(user.getUserId()))) {
            System.out.println("❌ Authorization failed. User ID not found in config_master: " + user.getUserId());
            throw new SecurityException("You are not authorized to edit this location");
        }

        // Fetch location details
        LocationDetails location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        // Apply updates from payload
        if (updates.containsKey("locationName")) {
            location.setLocationName((String) updates.get("locationName"));
        }

        if (updates.containsKey("shortLocationName")) {
            location.setShortLocationName((String) updates.get("shortLocationName"));
        }

        if (updates.containsKey("isActive")) {
            location.setIsActive(((String) updates.get("isActive")).toLowerCase().charAt(0));
        }

        // Update numeric fields safely
        if (updates.containsKey("bookingAllowedWindowInDays")) {
            location.setBookingAllowedWindowInDays(
                    updates.get("bookingAllowedWindowInDays") instanceof Number
                            ? ((Number) updates.get("bookingAllowedWindowInDays")).shortValue()
                            : Short.valueOf(updates.get("bookingAllowedWindowInDays").toString())
            );
        }

        if (updates.containsKey("noticeDurationToBookInMin")) {
            location.setNoticeDurationToBookInMin(
                    updates.get("noticeDurationToBookInMin") instanceof Number
                            ? ((Number) updates.get("noticeDurationToBookInMin")).byteValue()
                            : Byte.valueOf(updates.get("noticeDurationToBookInMin").toString())
            );
        }

        if (updates.containsKey("recurrenceCountAllowed")) {
            location.setRecurrenceCountAllowed(
                    updates.get("recurrenceCountAllowed") instanceof Number
                            ? ((Number) updates.get("recurrenceCountAllowed")).byteValue()
                            : Byte.valueOf(updates.get("recurrenceCountAllowed").toString())
            );
        }

        // Update admin users if provided
        if (updates.containsKey("adminUserIds")) {
            List<Short> adminUserIds = ((List<?>) updates.get("adminUserIds")).stream()
                    .map(id -> id instanceof Number ? ((Number) id).shortValue() : Short.valueOf(id.toString()))
                    .toList();

            // Fetch admin users from provided user IDs
            List<User> adminUsersList = (List<User>) userRepository.findAllById(adminUserIds);
            if (adminUsersList.isEmpty()) {
                throw new IllegalArgumentException("No valid admin users found for the provided IDs.");
            }

            // Convert admin users to JSON array containing only user IDs
            String adminUsersJson = new Gson().toJson(adminUsersList.stream()
                    .map(User::getUserId)
                    .toList());

            location.setAdminUsers(adminUsersJson);
        }

        System.out.println("✅ Location updated successfully for location ID: " + locationId);
        return locationRepository.save(location);
    }


    /**
     * Deletes a location after verifying the user's authorization.
     *
     * @param locationId     The ID of the location to be deleted.
     * @param authorization  The authorization token of the user.
     * @return A message indicating the success of the deletion.
     */
    @Transactional
    public String deleteLocation(Short locationId, String authorization) {
        // Extract email from token
        String email = tokenService.getEmailFromToken(authorization.replace("Bearer ", ""));
        System.out.println("🔍 Email extracted from token: " + email);

        // Retrieve authorized user IDs from config_master
        List<String> authorizedUserIds = configMasterRepository.getUserIdsFromConfigMaster();

        // Fetch user by email
        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if the extracted user ID is authorized
        if (!authorizedUserIds.contains(String.valueOf(user.getUserId()))) {
            System.out.println("❌ Authorization failed. User ID not found in config_master: " + user.getUserId());
            throw new SecurityException("You are not authorized to delete this location");
        }

        // Fetch location details
        LocationDetails location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        // Delete location
        locationRepository.delete(location);

        System.out.println("✅ Location deleted successfully for location ID: " + locationId);
        return "Location deleted successfully.";
    }

    /**
     * Retrieves all locations, ensuring that admin users are properly formatted.
     *
     * @return A list of all locations.
     */
    public List<LocationDetails> getAllLocations() {
        List<LocationDetails> locations = (List<LocationDetails>) locationRepository.findAll();
        for (LocationDetails location : locations) {
            location.setAdminUsers(new Gson().toJson(parseAdminUsers(location.getAdminUsers())));
        }
        return locations;
    }

    /**
     * Parses the admin users JSON and handles potential formatting issues.
     *
     * @param adminUsersJson The JSON string of admin users.
     * @return A list of admin users as maps.
     */
    private List<Map<String, String>> parseAdminUsers(String adminUsersJson) {
        try {
            if (adminUsersJson == null || adminUsersJson.isEmpty()) {
                System.out.println("❌ Empty adminUsers JSON");
                return List.of();
            }

            if (adminUsersJson.startsWith("\"")) {
                adminUsersJson = adminUsersJson.substring(1, adminUsersJson.length() - 1);
            }
            adminUsersJson = adminUsersJson.replace("\\", "");

            List<Map<String, String>> adminUsers = new Gson().fromJson(adminUsersJson,
                    new TypeToken<List<Map<String, String>>>() {}.getType());
            System.out.println("✅ Successfully parsed Admin Users: " + adminUsers);
            return adminUsers;
        } catch (Exception e) {
            System.out.println("❌ Error parsing adminUsers JSON: " + adminUsersJson);
            throw new RuntimeException("Error parsing adminUsers JSON: " + e.getMessage());
        }
    }
}
