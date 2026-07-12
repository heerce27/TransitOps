package com.transitops.model;

public class Vehicle {
    private String registrationNumber;
    private String name;
    private String model;
    private String type;
    private String capacity;
    private String status;

    public Vehicle() {
    }

    public Vehicle(String registrationNumber, String name, String model, String type, String capacity, String status) {
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.model = model;
        this.type = type;
        this.capacity = capacity;
        this.status = status;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
