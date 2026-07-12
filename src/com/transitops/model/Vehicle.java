package com.transitops.model;

import java.time.LocalDate;

public class Vehicle {
    private int vehicleId;
    private String vehicleNumber;
    private String vehicleType;
    private String make;
    private String model;
    private int manufactureYear;
    private String capacity;
    private String fuelType;
    private int odometerKm;
    private LocalDate purchaseDate;
    private LocalDate insuranceExpiry;
    private LocalDate fitnessExpiry;
    private String status;
    private Integer assignedDriverId;
    private String assignedDriverName;

    private String registrationNumber;
    private String name;
    private String type;

    public Vehicle() {
    }

    public Vehicle(String registrationNumber, String name, String model, String type, String capacity, String status) {
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.model = model;
        this.type = type;
        this.capacity = capacity;
        this.status = status;
        this.vehicleNumber = registrationNumber;
        this.vehicleType = type;
        this.make = name;
        this.manufactureYear = 2024;
        this.fuelType = "Diesel";
        this.odometerKm = 0;
        this.purchaseDate = LocalDate.now();
        this.insuranceExpiry = LocalDate.now().plusYears(1);
        this.fitnessExpiry = LocalDate.now().plusYears(1);
        this.assignedDriverId = null;
        this.assignedDriverName = "Unassigned";
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber != null ? vehicleNumber : registrationNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
        this.registrationNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType != null ? vehicleType : type;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
        this.type = vehicleType;
    }

    public String getMake() {
        return make != null ? make : name;
    }

    public void setMake(String make) {
        this.make = make;
        this.name = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(int manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public int getOdometerKm() {
        return odometerKm;
    }

    public void setOdometerKm(int odometerKm) {
        this.odometerKm = odometerKm;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getInsuranceExpiry() {
        return insuranceExpiry;
    }

    public void setInsuranceExpiry(LocalDate insuranceExpiry) {
        this.insuranceExpiry = insuranceExpiry;
    }

    public LocalDate getFitnessExpiry() {
        return fitnessExpiry;
    }

    public void setFitnessExpiry(LocalDate fitnessExpiry) {
        this.fitnessExpiry = fitnessExpiry;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAssignedDriverId() {
        return assignedDriverId;
    }

    public void setAssignedDriverId(Integer assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }

    public String getAssignedDriverName() {
        return assignedDriverName;
    }

    public void setAssignedDriverName(String assignedDriverName) {
        this.assignedDriverName = assignedDriverName;
    }

    public String getRegistrationNumber() {
        return registrationNumber != null ? registrationNumber : vehicleNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
        this.vehicleNumber = registrationNumber;
    }

    public String getName() {
        return name != null ? name : make;
    }

    public void setName(String name) {
        this.name = name;
        this.make = name;
    }

    public String getType() {
        return type != null ? type : vehicleType;
    }

    public void setType(String type) {
        this.type = type;
        this.vehicleType = type;
    }

    public String getCapacity() {
        return capacity == null ? "" : capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }
}
