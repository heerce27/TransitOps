package com.transitops.service;

import com.transitops.model.Vehicle;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VehicleService {
    private final Path vehiclesFile;

    public VehicleService() {
        this(Path.of("data", "vehicles.txt"));
    }

    public VehicleService(Path vehiclesFile) {
        this.vehiclesFile = vehiclesFile;
    }

    public List<Vehicle> loadVehicles() {
        try {
            if (!Files.exists(vehiclesFile)) {
                Files.createDirectories(vehiclesFile.getParent());
                Files.createFile(vehiclesFile);
                return new ArrayList<>();
            }

            List<String> lines = Files.readAllLines(vehiclesFile, StandardCharsets.UTF_8);
            List<Vehicle> vehicles = new ArrayList<>();
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", 6);
                if (parts.length == 6) {
                    vehicles.add(new Vehicle(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                }
            }
            return vehicles;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void addVehicle(String registration, String name, String model, String type, String capacity, String status) {
        List<Vehicle> vehicles = loadVehicles();
        vehicles.add(new Vehicle(registration, name, model, type, capacity, status));
        saveVehicles(vehicles);
    }

    private void saveVehicles(List<Vehicle> vehicles) {
        try {
            Files.createDirectories(vehiclesFile.getParent());
            List<String> lines = new ArrayList<>();
            for (Vehicle vehicle : vehicles) {
                lines.add(String.join("|", vehicle.getRegistrationNumber(), vehicle.getName(), vehicle.getModel(), vehicle.getType(), vehicle.getCapacity(), vehicle.getStatus()));
            }
            Files.write(vehiclesFile, lines, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }
}
