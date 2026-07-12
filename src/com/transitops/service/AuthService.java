package com.transitops.service;

import com.transitops.model.User;
import java.nio.file.Path;

public class AuthService {
    private final UserService userService;

    public AuthService() {
        this(Path.of("data", "users.txt"));
    }

    public AuthService(Path usersFile) {
        this.userService = new UserService(usersFile);
    }

    public User authenticate(String email, String password) {
        return userService.authenticate(email, password);
    }

    public User register(String fullName, String email, String password, String role) {
        return userService.register(fullName, email, password, role);
    }
}
