public class TripManagement {
    public enum TripStatus {
        DRAFT, DISPATCHED, COMPLETED, CANCELLED
    }

    public static class Trip {
        private String id;
        private String source;
        private String destination;
        private String assignedVehicle;
        private String assignedDriver;
        private double cargoWeight;
        private double plannedDistance;
        private TripStatus status;

        public Trip(String id, String source, String destination, double cargoWeight, double plannedDistance) {
            this.id = id;
            this.source = source;
            this.destination = destination;
            this.cargoWeight = cargoWeight;
            this.plannedDistance = plannedDistance;
            this.status = TripStatus.DRAFT;
        }

        public void assignVehicle(String vehicle) {
            this.assignedVehicle = vehicle;
        }

        public void assignDriver(String driver) {
            this.assignedDriver = driver;
        }

        public void dispatchTrip() {
            this.status = TripStatus.DISPATCHED;
        }

        public void completeTrip() {
            this.status = TripStatus.COMPLETED;
        }

        public void cancelTrip() {
            this.status = TripStatus.CANCELLED;
        }

        @Override
        public String toString() {
            return "Trip{" +
                    "id='" + id + '\'' +
                    ", source='" + source + '\'' +
                    ", destination='" + destination + '\'' +
                    ", assignedVehicle='" + assignedVehicle + '\'' +
                    ", assignedDriver='" + assignedDriver + '\'' +
                    ", cargoWeight=" + cargoWeight +
                    ", plannedDistance=" + plannedDistance +
                    ", status=" + status +
                    '}';
        }
    }

    public static void main(String[] args) {
        Trip trip = new Trip("TRIP-001", "Surat", "Ahmedabad", 1200.5, 250.0);
        trip.assignVehicle("MH-01-AB-1234");
        trip.assignDriver("Ravi Patel");
        trip.dispatchTrip();
        trip.completeTrip();

        System.out.println(trip);
    }
}
