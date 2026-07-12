package com.transitops.model;

public class Expense {

    private final String vehicle;
    private final String category;
    private final double amount;
    private final String date;
    private final String description;

    public Expense(String vehicle, String category,
                   double amount, String date,
                   String description) {

        this.vehicle = vehicle;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}