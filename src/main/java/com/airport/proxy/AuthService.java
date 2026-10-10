package com.airport.proxy;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// Hardcoded demo users (lab project - not real security).
@Service
public class AuthService {

    private final Map<String, User> users = new HashMap<>();

    public AuthService() {
        users.put("passenger1", new User("passenger1", "pass123", Role.PASSENGER));
        users.put("staff1", new User("staff1", "staff123", Role.STAFF));
        users.put("admin1", new User("admin1", "admin123", Role.ADMIN));
    }

    public User authenticate(String username, String password) {
        User u = username == null ? null : users.get(username);
        return (u != null && u.getPassword().equals(password)) ? u : null;
    }
}
