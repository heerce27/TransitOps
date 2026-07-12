package com.transitops.model;

public class Driver {
    private final String name;
    private final String licenseNumber;
    private final String phoneNumber;
    private final String status;

    public Driver(String name, String licenseNumber, String phoneNumber, String status) {
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatus() {
        return status;
    }
}
