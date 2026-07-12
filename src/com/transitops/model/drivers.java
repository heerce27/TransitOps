package com.transitops.model;

class DriverLegacy {
    private final String name;
    private final String licenseNumber;
    private final String licenseCategory;
    private final String licenseExpiryDate;
    private final String contactNumber;
    private final int safetyScore;
    private final String status;

    DriverLegacy(String name, String licenseNumber,
                 String licenseCategory, String licenseExpiryDate,
                 String contactNumber, int safetyScore,
                 String status) {
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.licenseCategory = licenseCategory;
        this.licenseExpiryDate = licenseExpiryDate;
        this.contactNumber = contactNumber;
        this.safetyScore = safetyScore;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getLicenseCategory() {
        return licenseCategory;
    }

    public String getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public int getSafetyScore() {
        return safetyScore;
    }

    public String getStatus() {
        return status;
    }
}