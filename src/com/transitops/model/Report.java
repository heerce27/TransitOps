package com.transitops.model;

public class Report {
    private final String reportType;
    private final String vehicle;
    private final String value;
    private final String date;
    private final String status;

    public Report(String reportType, String vehicle, String value, String date, String status) {
        this.reportType = reportType;
        this.vehicle = vehicle;
        this.value = value;
        this.date = date;
        this.status = status;
    }

    public String getReportType() {
        return reportType;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getValue() {
        return value;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
