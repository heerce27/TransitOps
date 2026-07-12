package com.transitops.dao;

import com.transitops.model.Vehicle;
import com.transitops.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    private static final String SELECT_BASE =
        "SELECT v.vehicle_id, v.vehicle_number, v.vehicle_type, v.make, v.model, " +
        "v.manufacture_year, v.capacity, v.fuel_type, v.odometer_km, v.purchase_date, " +
        "v.insurance_expiry, v.fitness_expiry, v.status, v.assigned_driver_id, d.full_name " +
        "FROM vehicles v LEFT JOIN drivers d ON v.assigned_driver_id = d.driver_id";

    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY v.vehicle_number ASC";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapRow(rs));
            }
        }
        return vehicles;
    }

    public Vehicle getVehicleById(int vehicleId) throws SQLException {
        String sql = SELECT_BASE + " WHERE v.vehicle_id = ?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean isVehicleNumberTaken(String vehicleNumber, Integer excludeVehicleId) throws SQLException {
        String sql = "SELECT vehicle_id FROM vehicles WHERE vehicle_number = ? AND vehicle_id != ?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, vehicleNumber);
            ps.setInt(2, excludeVehicleId == null ? -1 : excludeVehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int addVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (vehicle_number, vehicle_type, make, model, " +
                     "manufacture_year, capacity, fuel_type, odometer_km, purchase_date, " +
                     "insurance_expiry, fitness_expiry, status, assigned_driver_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindVehicleParams(ps, vehicle);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET vehicle_number = ?, vehicle_type = ?, make = ?, " +
                     "model = ?, manufacture_year = ?, capacity = ?, fuel_type = ?, " +
                     "odometer_km = ?, purchase_date = ?, insurance_expiry = ?, " +
                     "fitness_expiry = ?, status = ?, assigned_driver_id = ? WHERE vehicle_id = ?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bindVehicleParams(ps, vehicle);
            ps.setInt(14, vehicle.getVehicleId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteVehicle(int vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            return ps.executeUpdate() > 0;
        }
    }

    private void bindVehicleParams(PreparedStatement ps, Vehicle v) throws SQLException {
        ps.setString(1, v.getVehicleNumber());
        ps.setString(2, v.getVehicleType());
        ps.setString(3, v.getMake());
        ps.setString(4, v.getModel());
        ps.setInt(5, v.getManufactureYear());
        ps.setString(6, v.getCapacity());
        ps.setString(7, v.getFuelType());
        ps.setInt(8, v.getOdometerKm());
        ps.setDate(9, Date.valueOf(v.getPurchaseDate()));
        ps.setDate(10, Date.valueOf(v.getInsuranceExpiry()));
        ps.setDate(11, Date.valueOf(v.getFitnessExpiry()));
        ps.setString(12, v.getStatus());
        if (v.getAssignedDriverId() != null) {
            ps.setInt(13, v.getAssignedDriverId());
        } else {
            ps.setNull(13, Types.INTEGER);
        }
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("vehicle_id"));
        v.setVehicleNumber(rs.getString("vehicle_number"));
        v.setVehicleType(rs.getString("vehicle_type"));
        v.setMake(rs.getString("make"));
        v.setModel(rs.getString("model"));
        v.setManufactureYear(rs.getInt("manufacture_year"));
        v.setCapacity(rs.getString("capacity"));
        v.setFuelType(rs.getString("fuel_type"));
        v.setOdometerKm(rs.getInt("odometer_km"));
        v.setPurchaseDate(rs.getDate("purchase_date").toLocalDate());
        v.setInsuranceExpiry(rs.getDate("insurance_expiry").toLocalDate());
        v.setFitnessExpiry(rs.getDate("fitness_expiry").toLocalDate());
        v.setStatus(rs.getString("status"));
        int driverId = rs.getInt("assigned_driver_id");
        v.setAssignedDriverId(rs.wasNull() ? null : driverId);
        v.setAssignedDriverName(rs.getString("full_name"));
        return v;
    }
}
