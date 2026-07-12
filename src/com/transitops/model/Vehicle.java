package model;

import java.time.LocalDate;

public class Vehicle {

    private int vehicleId;
    private String vehicleNumber;
    private String vehicleType;      // BUS, VAN, TRUCK, CAR
    private String make;
    private String model;
    private int manufactureYear;
    private int capacity;
    private String fuelType;         // DIESEL, PETROL, CNG, ELECTRIC
    private int odometerKm;
    private LocalDate purchaseDate;
    private LocalDate insuranceExpiry;
    private LocalDate fitnessExpiry;
    private String status;           // ACTIVE, IN_MAINTENANCE, INACTIVE
    private Integer assignedDriverId;
    private String assignedDriverName; // populated via join for display only

    public Vehicle() {
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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
}
