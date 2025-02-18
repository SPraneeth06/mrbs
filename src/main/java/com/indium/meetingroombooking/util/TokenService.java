package com.indium.meetingroombooking.util;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    public String processToken(String token) {
        try {
            // Decode token and extract details
            String name = TokenGenerate.extractFieldFromToken(token, "name");
            String email = TokenGenerate.extractFieldFromToken(token, "preferred_username");

            // Log and return user details
            return "User Details: Name = " + name + ", Email = " + email;

        } catch (Exception e) {
            return "Failed to process token: " + e.getMessage();
        }
    }
    public String getEmailFromToken(String token) {
        try {
            return TokenGenerate.extractFieldFromToken(token, "preferred_username");
        } catch (Exception e) {
            return null;
        }
    }
}

