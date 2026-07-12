package com.transitops.service;

public class Fuel {
    private String vehicleNumber;
    private String fuelType;
    private double quantityLiters;
    private double cost;
    private String date;
    private String notes;

    public Fuel() {
    }

    public Fuel(String vehicleNumber, String fuelType, double quantityLiters,
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
