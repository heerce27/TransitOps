package com.transitops.service;

import com.transitops.model.Report;

import java.util.ArrayList;
import java.util.List;

public class ReportService {

    private final List<Report> reports;

    public ReportService() {

        reports = new ArrayList<>();

        reports.add(new Report(
                "Fleet Utilization",
                "Truck-01",
                "85%",
                "2026-07-10",
                "Good"
        ));

        reports.add(new Report(
                "Fuel Efficiency",
                "Van-05",
                "12 km/L",
                "2026-07-09",
                "Efficient"
        ));

        reports.add(new Report(
                "Operational Cost",
                "Truck-02",
                "₹15000",
                "2026-07-08",
                "Normal"
        ));

        reports.add(new Report(
                "Maintenance Cost",
                "Van-05",
                "₹2500",
                "2026-07-10",
                "Completed"
        ));

        reports.add(new Report(
                "Vehicle ROI",
                "Truck-01",
                "18%",
                "2026-07-11",
                "Profitable"
        ));

        reports.add(new Report(
                "Expense Summary",
                "Truck-02",
                "₹8500",
                "2026-07-11",
                "Recorded"
        ));
    }

    public List<Report> getAllReports() {
        return reports;
    }


    public List<Report> getReportsByType(
            String reportType) {

        List<Report> filteredReports =
                new ArrayList<>();


        for (Report report : reports) {

            if (report.getReportType()
                    .equalsIgnoreCase(reportType)) {

                filteredReports.add(report);

            }
        }


        return filteredReports;
    }


    public int getTotalReports() {
        return reports.size();
    }
}
