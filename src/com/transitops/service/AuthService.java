package com.transitops.service;

import com.transitops.model.User;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private final List<User> users = new ArrayList<>();

    public AuthService() {
        users.add(new User("fleetmanager@transitops.com", "Fleet@2026", "Amina Khan", "Fleet Manager"));
        users.add(new User("driver@transitops.com", "Driver@2026", "Daniel Cruz", "Driver"));
        users.add(new User("safety@transitops.com", "Safety@2026", "Nadia Brooks", "Safety Officer"));
        users.add(new User("analyst@transitops.com", "Analyst@2026", "Samir Hassan", "Financial Analyst"));
    }

    public User authenticate(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
