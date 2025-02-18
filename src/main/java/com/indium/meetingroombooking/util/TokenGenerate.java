package com.indium.meetingroombooking.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Base64;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;



import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenGenerate {

    /**
     * Decodes a Base64-encoded token and extracts its payload.
     * Ignores signature verification.
     *
     * @param token The JWT token.
     * @return A map containing the token payload as key-value pairs.
     * @throws RuntimeException if the token cannot be decoded.
     */
    public static Map<String, Object> decodeToken(String token) {
        try {
            // Split the token into parts (header.payload.signature)
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid token format");
            }

            // Decode payload (second part)
            String payload = new String(Base64.getDecoder().decode(parts[1]));

            // Convert payload to a Map
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payload, Map.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to decode token: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts specific fields from the decoded token payload.
     *
     * @param token The JWT token.
     * @param field The field to extract (e.g., "name", "preferred_username").
     * @return The value of the requested field, or null if not found.
     */
    public static String extractFieldFromToken(String token, String field) {
        Map<String, Object> payload = decodeToken(token);
        return payload.getOrDefault(field, "").toString();
    }
}


