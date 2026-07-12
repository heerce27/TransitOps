package service;

import dao.VehicleDAO;
import model.Vehicle;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class VehicleService {

    private final VehicleDAO vehicleDAO = new VehicleDAO();

    public List<Vehicle> getAllVehicles() throws SQLException {
        return vehicleDAO.getAllVehicles();
    }

    public Vehicle getVehicleById(int vehicleId) throws SQLException {
        return vehicleDAO.getVehicleById(vehicleId);
    }

    public int createVehicle(Vehicle vehicle) throws SQLException {
        validate(vehicle, null);
        if (vehicle.getStatus() == null || vehicle.getStatus().isBlank()) {
            vehicle.setStatus("ACTIVE");
        }
        return vehicleDAO.addVehicle(vehicle);
    }

    public boolean updateVehicle(Vehicle vehicle) throws SQLException {
        validate(vehicle, vehicle.getVehicleId());
        return vehicleDAO.updateVehicle(vehicle);
    }

    public boolean deleteVehicle(int vehicleId) throws SQLException {
        return vehicleDAO.deleteVehicle(vehicleId);
    }

    /** Days remaining until insurance expiry. Negative means already expired. */
    public long daysUntilInsuranceExpiry(Vehicle vehicle) {
        return LocalDate.now().until(vehicle.getInsuranceExpiry(), java.time.temporal.ChronoUnit.DAYS);
    }

    /** Days remaining until fitness certificate expiry. Negative means already expired. */
    public long daysUntilFitnessExpiry(Vehicle vehicle) {
        return LocalDate.now().until(vehicle.getFitnessExpiry(), java.time.temporal.ChronoUnit.DAYS);
    }

    private void validate(Vehicle vehicle, Integer excludeId) throws SQLException {
        if (vehicle.getVehicleNumber() == null || vehicle.getVehicleNumber().isBlank()) {
            throw new IllegalArgumentException("Vehicle number is required.");
        }
        if (vehicle.getVehicleType() == null || vehicle.getVehicleType().isBlank()) {
            throw new IllegalArgumentException("Vehicle type is required.");
        }
        if (vehicle.getMake() == null || vehicle.getMake().isBlank()) {
            throw new IllegalArgumentException("Make is required.");
        }
        if (vehicle.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        if (vehicle.getInsuranceExpiry() == null) {
            throw new IllegalArgumentException("Insurance expiry date is required.");
        }
        if (vehicle.getFitnessExpiry() == null) {
            throw new IllegalArgumentException("Fitness certificate expiry date is required.");
        }
        if (vehicleDAO.isVehicleNumberTaken(vehicle.getVehicleNumber(), excludeId)) {
            throw new IllegalArgumentException("Vehicle number already exists in the fleet.");
        }
    }
}
