package com.transitops.service;

import java.util.ArrayList;
import java.util.List;

public class FuelService {

    private final List<FuelEntry> fuelEntries;

    public FuelService() {
        fuelEntries = new ArrayList<>();

        fuelEntries.add(new FuelEntry(
                "Truck-01",
                "Diesel",
                120.5,
                14500.0,
                "2026-07-10",
                "Full tank refill"
        ));

        fuelEntries.add(new FuelEntry(
                "Van-05",
                "Petrol",
                48.0,
                5600.0,
                "2026-07-11",
                "City route refill"
        ));
    }

    public List<FuelEntry> getAllFuelEntries() {
        return fuelEntries;
    }

    public void addFuelEntry(FuelEntry entry) {
        fuelEntries.add(entry);
    }

    public boolean deleteFuelEntry(String vehicleNumber, String date) {
        for (FuelEntry entry : fuelEntries) {
            if (entry.getVehicleNumber().equalsIgnoreCase(vehicleNumber)
                    && entry.getDate().equals(date)) {
                fuelEntries.remove(entry);
                return true;
            }
        }
        return false;
    }

    public double calculateTotalFuelCost() {
        double total = 0;
        for (FuelEntry entry : fuelEntries) {
            total += entry.getCost();
        }
        return total;
    }

    public double calculateTotalQuantity() {
        double total = 0;
        for (FuelEntry entry : fuelEntries) {
            total += entry.getQuantityLiters();
        }
        return total;
    }

    public static class FuelEntry {
        private String vehicleNumber;
        private String fuelType;
        private double quantityLiters;
        private double cost;
        private String date;
        private String notes;

        public FuelEntry(String vehicleNumber, String fuelType, double quantityLiters,
                         double cost, String date, String notes) {
            this.vehicleNumber = vehicleNumber;
            this.fuelType = fuelType;
            this.quantityLiters = quantityLiters;
            this.cost = cost;
            this.date = date;
            this.notes = notes;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
        }

        public String getFuelType() {
            return fuelType;
        }

        public void setFuelType(String fuelType) {
            this.fuelType = fuelType;
        }

        public double getQuantityLiters() {
            return quantityLiters;
        }

        public void setQuantityLiters(double quantityLiters) {
            this.quantityLiters = quantityLiters;
        }

        public double getCost() {
            return cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}
