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
        if (email == null || password == null) {
            return null;
        }
        try {
            ensureDatabase();
            String sql = "SELECT email, password, name, role FROM users WHERE lower(email) = '" + escapeSqlLiteral(email.toLowerCase()) + "' AND password = '" + escapeSqlLiteral(password) + "';";
            List<String> rows = runSql(sql);
            for (String row : rows) {
                String[] parts = row.split("\\|", 4);
                if (parts.length == 4) {
                    return new User(parts[0], parts[1], parts[2], parts[3]);
                }
            }
        } catch (IOException | InterruptedException ignored) {
        }
        return null;
    }

    public User register(String fullName, String email, String password, String role) {
        if (fullName == null || email == null || password == null || role == null) {
            return null;
        }
        try {
            ensureDatabase();
            if (findByEmail(email) != null) {
                return null;
            }
            String sql = "INSERT INTO users (email, password, name, role) VALUES ('"
                    + escapeSqlLiteral(email)
                    + "', '"
                    + escapeSqlLiteral(password)
                    + "', '"
                    + escapeSqlLiteral(fullName)
                    + "', '"
                    + escapeSqlLiteral(role)
                    + "');";
            executeSql(sql);
            return new User(email, password, fullName, role);
        } catch (IOException | InterruptedException ignored) {
            return null;
        }
    }

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try {
            ensureDatabase();
            List<String> rows = runSql("SELECT email, password, name, role FROM users ORDER BY email;");
            for (String row : rows) {
                String[] parts = row.split("\\|", 4);
                if (parts.length == 4) {
                    users.add(new User(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException | InterruptedException ignored) {
        }
        return users;
    }

    private User findByEmail(String email) {
        try {
            String sql = "SELECT email, password, name, role FROM users WHERE lower(email) = '" + escapeSqlLiteral(email.toLowerCase()) + "';";
            List<String> rows = runSql(sql);
            for (String row : rows) {
                String[] parts = row.split("\\|", 4);
                if (parts.length == 4) {
                    return new User(parts[0], parts[1], parts[2], parts[3]);
                }
            }
        } catch (IOException | InterruptedException ignored) {
        }
        return null;
    }

    private void ensureDatabase() throws IOException, InterruptedException {
        Files.createDirectories(usersFile.getParent());
        executeSql("CREATE TABLE IF NOT EXISTS users (email TEXT PRIMARY KEY, password TEXT NOT NULL, name TEXT NOT NULL, role TEXT NOT NULL);");
    }

    private void executeSql(String statement) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("sqlite3", usersFile.toAbsolutePath().toString(), statement);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("sqlite3 failed: " + output);
        }
    }

    private List<String> runSql(String statement) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("sqlite3", "-separator", "|", usersFile.toAbsolutePath().toString(), statement);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("sqlite3 failed: " + output);
        }
        List<String> rows = new ArrayList<>();
        for (String line : output.split("\\R")) {
            if (!line.trim().isEmpty()) {
                rows.add(line);
            }
        }
        return rows;
    }

    private String escapeSqlLiteral(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("'", "''");
    }
}
