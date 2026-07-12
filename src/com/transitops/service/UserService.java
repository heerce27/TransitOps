package com.transitops.service;

import com.transitops.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final Path usersFile;

    public UserService(Path usersFile) {
        this.usersFile = usersFile;
    }

    public User authenticate(String email, String password) {
        for (User user : loadUsers()) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public User register(String fullName, String email, String password, String role) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return null;
            }
        }
        User newUser = new User(email, password, fullName, role);
        users.add(newUser);
        saveUsers(users);
        return newUser;
    }

    public List<User> loadUsers() {
        try {
            if (!Files.exists(usersFile)) {
                Files.createDirectories(usersFile.getParent());
                Files.createFile(usersFile);
                return new ArrayList<>();
            }
            List<String> lines = Files.readAllLines(usersFile, StandardCharsets.UTF_8);
            List<User> users = new ArrayList<>();
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", 4);
                if (parts.length == 4) {
                    users.add(new User(parts[0], parts[1], parts[2], parts[3]));
                }
            }
            return users;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveUsers(List<User> users) {
        try {
            Files.createDirectories(usersFile.getParent());
            List<String> lines = new ArrayList<>();
            for (User user : users) {
                lines.add(user.getEmail() + "|" + user.getPassword() + "|" + user.getName() + "|" + user.getRole());
            }
            Files.write(usersFile, lines, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }
}
