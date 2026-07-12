package com.transitops.service;

import com.transitops.model.Driver;

import java.util.ArrayList;
import java.util.List;

public class DriverService {

    private final List<Driver> drivers;

    public DriverService() {

        drivers = new ArrayList<>();

        drivers.add(new Driver(
                "Alex Johnson",
                "DL12345678",
                "HMV",
                "2027-12-20",
                "9876543210",
                92,
                "Available"
        ));

        drivers.add(new Driver(
                "Rahul Sharma",
                "DL87654321",
                "LMV",
                "2028-05-15",
                "9876501234",
                88,
                "Off Duty"
        ));
    }

    public List<Driver> getAllDrivers() {
        return drivers;
    }

    public void addDriver(Driver driver) {
        drivers.add(driver);
    }

    public boolean deleteDriver(String licenseNumber) {

        for (Driver driver : drivers) {

            if (driver.getLicenseNumber()
                    .equalsIgnoreCase(licenseNumber)) {

                drivers.remove(driver);
                return true;
            }
        }

        return false;
    }

    public Driver findDriver(String licenseNumber) {

        for (Driver driver : drivers) {

            if (driver.getLicenseNumber()
                    .equalsIgnoreCase(licenseNumber)) {

                return driver;
            }
        }

        return null;
    }
}
