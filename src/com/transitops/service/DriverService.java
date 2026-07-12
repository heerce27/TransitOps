package com.transitops.service;

import com.transitops.model.Driver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DriverService {
    private final Path driversFile;

    public DriverService() {
        this(Path.of("data", "drivers.txt"));
    }

    public DriverService(Path driversFile) {
        this.driversFile = driversFile;
    }

    public List<Driver> loadDrivers() {
        try {
            if (!Files.exists(driversFile)) {
                Files.createDirectories(driversFile.getParent());
                Files.createFile(driversFile);
                return new ArrayList<>();
            }

            List<String> lines = Files.readAllLines(driversFile, StandardCharsets.UTF_8);
            List<Driver> drivers = new ArrayList<>();
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", 4);
                if (parts.length == 4) {
                    drivers.add(new Driver(parts[0], parts[1], parts[2], parts[3]));
                }
            }
            return drivers;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void addDriver(String name, String license, String phone, String status) {
        List<Driver> drivers = loadDrivers();
        drivers.add(new Driver(name, license, phone, status));
        saveDrivers(drivers);
    }

    private void saveDrivers(List<Driver> drivers) {
        try {
            Files.createDirectories(driversFile.getParent());
            List<String> lines = new ArrayList<>();
            for (Driver driver : drivers) {
                lines.add(String.join("|", driver.getName(), driver.getLicenseNumber(), driver.getPhoneNumber(), driver.getStatus()));
            }
            Files.write(driversFile, lines, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }
}
