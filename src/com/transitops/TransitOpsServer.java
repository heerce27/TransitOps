package com.transitops;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.transitops.model.Driver;
import com.transitops.model.User;
import com.transitops.model.Vehicle;
import com.transitops.service.AuthService;
import com.transitops.service.DriverService;
import com.transitops.service.VehicleService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TransitOpsServer {
    private static final int PORT = 8080;
    private static final Path WEB_ROOT = Paths.get("WebContent").toAbsolutePath().normalize();
    private static final Path DATA_DIR = Paths.get("data").toAbsolutePath().normalize();
    private static final Map<String, User> SESSIONS = new HashMap<>();
    private static final AuthService AUTH_SERVICE = new AuthService(DATA_DIR.resolve("users.txt"));
    private static final VehicleService VEHICLE_SERVICE = new VehicleService(DATA_DIR.resolve("vehicles.txt"));
    private static final DriverService DRIVER_SERVICE = new DriverService(DATA_DIR.resolve("drivers.txt"));
    private static final List<MaintenanceRecord> MAINTENANCE_RECORDS = new ArrayList<>();
    private static final List<TripRecord> TRIP_RECORDS = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Files.createDirectories(DATA_DIR);
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new StaticFileHandler("index.html"));
        server.createContext("/index.html", new StaticFileHandler("index.html"));
        server.createContext("/login", new LoginHandler());
        server.createContext("/register", new RegisterHandler());
        server.createContext("/dashboard", new DashboardHandler());
server.createContext("/maintenance", new MaintenanceHandler());
server.createContext("/fuel", new StaticFileHandler("fuel.html"));
server.createContext("/trips", new TripHandler());
server.createContext("/vehicles", new VehicleHandler());
server.createContext("/drivers", new DriverHandler());
        server.createContext("/logout", new LogoutHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("TransitOps server started at http://localhost:" + PORT);
    }

    private static class StaticFileHandler implements HttpHandler {
        private final String fileName;

        StaticFileHandler(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method not allowed");
                return;
            }
            String requestPath = exchange.getRequestURI().getPath();
            String targetPath = requestPath.equals("/") ? fileName : requestPath.substring(1);
            Path file = WEB_ROOT.resolve(targetPath).normalize();
            if (!file.startsWith(WEB_ROOT)) {
                sendText(exchange, 403, "Forbidden");
                return;
            }
            if (Files.exists(file) && Files.isRegularFile(file)) {
                String contentType = determineContentType(file);
                byte[] content = Files.readAllBytes(file);
                sendBytes(exchange, 200, content, contentType);
            } else {
                sendText(exchange, 404, "File not found");
            }
        }
    }

    private static String determineContentType(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        if (name.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (name.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }
        return "text/html; charset=UTF-8";
    }

    private static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                serveFile(exchange, "login.html");
                return;
            }
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method not allowed");
                return;
            }

            Map<String, String> form = readForm(exchange);
            String email = form.getOrDefault("email", "").trim();
            String password = form.getOrDefault("password", "").trim();
            User user = AUTH_SERVICE.authenticate(email, password);

            if (user == null) {
                String html = Files.readString(WEB_ROOT.resolve("login.html"));
                html = html.replace("{{MESSAGE}}", "<p class=\"message error\">Invalid email or password.</p>")
                        .replace("{{FORM_ACTION}}", "/login");
                sendText(exchange, 401, html);
                return;
            }

            String sessionId = UUID.randomUUID().toString();
            SESSIONS.put(sessionId, user);
            exchange.getResponseHeaders().add("Set-Cookie", "transitops_session=" + sessionId + "; HttpOnly; Path=/");
            exchange.getResponseHeaders().add("Location", "/dashboard");
            sendText(exchange, 303, "Redirecting to dashboard...");
        }
    }

    private static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                serveFile(exchange, "register.html");
                return;
            }
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method not allowed");
                return;
            }

            Map<String, String> form = readForm(exchange);
            String fullName = form.getOrDefault("fullName", "").trim();
            String email = form.getOrDefault("email", "").trim();
            String password = form.getOrDefault("password", "").trim();
            String role = form.getOrDefault("role", "Fleet Manager").trim();

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                String html = Files.readString(WEB_ROOT.resolve("register.html"));
                html = html.replace("{{MESSAGE}}", "<p class=\"message error\">Please complete all fields.</p>")
                        .replace("{{FORM_ACTION}}", "/register");
                sendText(exchange, 400, html);
                return;
            }

            User created = AUTH_SERVICE.register(fullName, email, password, role);
            if (created == null) {
                String html = Files.readString(WEB_ROOT.resolve("register.html"));
                html = html.replace("{{MESSAGE}}", "<p class=\"message error\">This email is already registered.</p>")
                        .replace("{{FORM_ACTION}}", "/register");
                sendText(exchange, 409, html);
                return;
            }

            String sessionId = UUID.randomUUID().toString();
            SESSIONS.put(sessionId, created);
            exchange.getResponseHeaders().add("Set-Cookie", "transitops_session=" + sessionId + "; HttpOnly; Path=/");
            exchange.getResponseHeaders().add("Location", "/dashboard");
            sendText(exchange, 303, "Welcome to TransitOps");
        }
    }

    private static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method not allowed");
                return;
            }

            User user = getAuthenticatedUser(exchange);
            if (user == null) {
                exchange.getResponseHeaders().add("Location", "/login");
                sendText(exchange, 303, "Please log in first");
                return;
            }

            String html = Files.readString(WEB_ROOT.resolve("dashboard.html"));
            html = html.replace("{{USER_NAME}}", user.getName())
                    .replace("{{USER_ROLE}}", user.getRole())
                    .replace("{{USER_EMAIL}}", user.getEmail());
            sendText(exchange, 200, html);
        }
    }

    private static class MaintenanceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            User user = getAuthenticatedUser(exchange);
            if (user == null) {
                exchange.getResponseHeaders().add("Location", "/login");
                sendText(exchange, 303, "Please log in first");
                return;
            }

            if ("GET".equals(exchange.getRequestMethod())) {
                String html = Files.readString(WEB_ROOT.resolve("maintenance.html"));
                html = html.replace("{{USER_NAME}}", user.getName())
                        .replace("{{USER_ROLE}}", user.getRole())
                        .replace("{{USER_EMAIL}}", user.getEmail())
                        .replace("{{MESSAGE}}", "")
                        .replace("{{MAINTENANCE_LIST}}", buildMaintenanceList());
                sendText(exchange, 200, html);
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> form = readForm(exchange);
                String vehicle = form.getOrDefault("vehicle", "").trim();
                String title = form.getOrDefault("title", "").trim();
                String date = form.getOrDefault("date", "").trim();
                String description = form.getOrDefault("description", "").trim();
                String status = form.getOrDefault("status", "Scheduled").trim();

                String message;
                if (vehicle.isEmpty() || title.isEmpty()) {
                    message = "<p class=\"message error\">Please enter both vehicle and maintenance title.</p>";
                } else {
                    MAINTENANCE_RECORDS.add(new MaintenanceRecord(vehicle, title, date, description, status));
                    message = "<p class=\"message success\">Maintenance request saved successfully.</p>";
                }

                String html = Files.readString(WEB_ROOT.resolve("maintenance.html"));
                html = html.replace("{{USER_NAME}}", user.getName())
                        .replace("{{USER_ROLE}}", user.getRole())
                        .replace("{{USER_EMAIL}}", user.getEmail())
                        .replace("{{MESSAGE}}", message)
                        .replace("{{MAINTENANCE_LIST}}", buildMaintenanceList());
                sendText(exchange, 200, html);
                return;
            }

            sendText(exchange, 405, "Method not allowed");
        }
    }

    private static class TripHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            User user = getAuthenticatedUser(exchange);
            if (user == null) {
                exchange.getResponseHeaders().add("Location", "/login");
                sendText(exchange, 303, "Please log in first");
                return;
            }

            if ("GET".equals(exchange.getRequestMethod())) {
                String html = Files.readString(WEB_ROOT.resolve("trips.html"));
                html = html.replace("{{USER_NAME}}", user.getName())
                        .replace("{{USER_ROLE}}", user.getRole())
                        .replace("{{USER_EMAIL}}", user.getEmail())
                        .replace("{{MESSAGE}}", "")
                        .replace("{{TRIP_LIST}}", buildTripList());
                sendText(exchange, 200, html);
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> form = readForm(exchange);
                String route = form.getOrDefault("route", "").trim();
                String vehicle = form.getOrDefault("vehicle", "").trim();
                String driver = form.getOrDefault("driver", "").trim();
                String status = form.getOrDefault("status", "Scheduled").trim();

                String message;
                if (route.isEmpty() || vehicle.isEmpty()) {
                    message = "<p class=\"message error\">Please enter at least the route and vehicle.</p>";
                } else {
                    TRIP_RECORDS.add(new TripRecord(route, vehicle, driver, status));
                    message = "<p class=\"message success\">Trip saved successfully.</p>";
                }

                String html = Files.readString(WEB_ROOT.resolve("trips.html"));
                html = html.replace("{{USER_NAME}}", user.getName())
                        .replace("{{USER_ROLE}}", user.getRole())
                        .replace("{{USER_EMAIL}}", user.getEmail())
                        .replace("{{MESSAGE}}", message)
                        .replace("{{TRIP_LIST}}", buildTripList());
                sendText(exchange, 200, html);
                return;
            }

            sendText(exchange, 405, "Method not allowed");
        }
    }

    private static class VehicleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            User user = getAuthenticatedUser(exchange);
            if (user == null) {
                exchange.getResponseHeaders().add("Location", "/login");
                sendText(exchange, 303, "Please log in first");
                return;
            }

            if ("GET".equals(exchange.getRequestMethod())) {
                String html = Files.readString(WEB_ROOT.resolve("vehicles.html"));
                html = html.replace("{{USER_NAME}}", user.getName())
                        .replace("{{USER_ROLE}}", user.getRole())
                        .replace("{{USER_EMAIL}}", user.getEmail())
                        .replace("{{MESSAGE}}", "")
                        .replace("{{VEHICLE_LIST}}", buildVehicleList());
                sendText(exchange, 200, html);
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> form = readForm(exchange);
                String registration = form.getOrDefault("registration", "").trim();
                String name = form.getOrDefault("name", "").trim();
                String model = form.getOrDefault("model", "").trim();
                String type = form.getOrDefault("type", "Bus").trim();
                String capacity = form.getOrDefault("capacity", "").trim();
                String status = form.getOrDefault("status", "Available").trim();
                String message;
                if (registration.isEmpty() || name.isEmpty()) {
                    message = "<p class=\"message error\">Please enter registration and name.</p>";
                } else {
                    VEHICLE_SERVICE.addVehicle(registration, name, model, type, capacity, status);
                    message = "<p class=\"message success\">Vehicle saved.</p>";
                }
                String html = Files.readString(WEB_ROOT.resolve("vehicles.html"));
                html = html.replace("{{USER_NAME}}", user.getName())
                        .replace("{{USER_ROLE}}", user.getRole())
                        .replace("{{USER_EMAIL}}", user.getEmail())
                        .replace("{{MESSAGE}}", message)
                        .replace("{{VEHICLE_LIST}}", buildVehicleList());
                sendText(exchange, 200, html);
                return;
            }

            sendText(exchange, 405, "Method not allowed");
        }
    }

    private static class DriverHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            User user = getAuthenticatedUser(exchange);
            if (user == null) {
                exchange.getResponseHeaders().add("Location", "/login");
                sendText(exchange, 303, "Please log in first");
                return;
            }

            if ("GET".equals(exchange.getRequestMethod())) {
                String html = Files.readString(WEB_ROOT.resolve("drivers.html"));
                html = html.replace("{{USER_NAME}}", user.getName())
                        .replace("{{USER_ROLE}}", user.getRole())
                        .replace("{{USER_EMAIL}}", user.getEmail())
                        .replace("{{MESSAGE}}", "")
                        .replace("{{DRIVER_LIST}}", buildDriverList());
                sendText(exchange, 200, html);
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> form = readForm(exchange);
                String name = form.getOrDefault("name", "").trim();
                String license = form.getOrDefault("license", "").trim();
                String phone = form.getOrDefault("phone", "").trim();
                String status = form.getOrDefault("status", "Available").trim();
                String message;
                if (name.isEmpty() || license.isEmpty()) {
                    message = "<p class=\"message error\">Please enter driver name and license.</p>";
                } else {
                    DRIVER_SERVICE.addDriver(name, license, phone, status);
                    message = "<p class=\"message success\">Driver saved.</p>";
                }
                String html = Files.readString(WEB_ROOT.resolve("drivers.html"));
                html = html.replace("{{USER_NAME}}", user.getName())
                        .replace("{{USER_ROLE}}", user.getRole())
                        .replace("{{USER_EMAIL}}", user.getEmail())
                        .replace("{{MESSAGE}}", message)
                        .replace("{{DRIVER_LIST}}", buildDriverList());
                sendText(exchange, 200, html);
                return;
            }

            sendText(exchange, 405, "Method not allowed");
        }
    }

    private static class LogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method not allowed");
                return;
            }
            String sessionId = getSessionId(exchange);
            if (sessionId != null) {
                SESSIONS.remove(sessionId);
            }
            exchange.getResponseHeaders().add("Set-Cookie", "transitops_session=; Max-Age=0; Path=/");
            exchange.getResponseHeaders().add("Location", "/login");
            sendText(exchange, 303, "Logged out");
        }
    }

    private static String buildMaintenanceList() {
        if (MAINTENANCE_RECORDS.isEmpty()) {
            return "<li>No maintenance records yet.</li>";
        }
        StringBuilder builder = new StringBuilder();
        for (MaintenanceRecord record : MAINTENANCE_RECORDS) {
            builder.append("<li>")
                    .append(escapeHtml(record.vehicle))
                    .append(" - ")
                    .append(escapeHtml(record.title))
                    .append(" | Date: ")
                    .append(escapeHtml(record.date))
                    .append(" | Status: ")
                    .append(escapeHtml(record.status))
                    .append("</li>");
        }
        return builder.toString();
    }

    private static String buildTripList() {
        if (TRIP_RECORDS.isEmpty()) {
            return "<li>No trips recorded yet.</li>";
        }
        StringBuilder builder = new StringBuilder();
        for (TripRecord record : TRIP_RECORDS) {
            builder.append("<li>")
                    .append(escapeHtml(record.route))
                    .append(" | Vehicle: ")
                    .append(escapeHtml(record.vehicle))
                    .append(" | Driver: ")
                    .append(escapeHtml(record.driver))
                    .append(" | Status: ")
                    .append(escapeHtml(record.status))
                    .append("</li>");
        }
        return builder.toString();
    }

    private static String buildVehicleList() {
        List<Vehicle> vehicles = VEHICLE_SERVICE.loadVehicles();
        if (vehicles.isEmpty()) {
            return "<tr><td colspan='5'>No vehicles registered yet.</td></tr>";
        }
        StringBuilder builder = new StringBuilder();
        for (Vehicle vehicle : vehicles) {
            builder.append("<tr>")
                    .append("<td>").append(escapeHtml(vehicle.getRegistrationNumber())).append("</td>")
                    .append("<td>").append(escapeHtml(vehicle.getName())).append("</td>")
                    .append("<td>").append(escapeHtml(vehicle.getType())).append("</td>")
                    .append("<td>").append(escapeHtml(vehicle.getCapacity())).append("</td>")
                    .append("<td>").append(escapeHtml(vehicle.getStatus())).append("</td>")
                    .append("</tr>");
        }
        return builder.toString();
    }

    private static String buildDriverList() {
        List<Driver> drivers = DRIVER_SERVICE.loadDrivers();
        if (drivers.isEmpty()) {
            return "<tr><td colspan='4'>No drivers registered yet.</td></tr>";
        }
        StringBuilder builder = new StringBuilder();
        for (Driver driver : drivers) {
            builder.append("<tr>")
                    .append("<td>").append(escapeHtml(driver.getName())).append("</td>")
                    .append("<td>").append(escapeHtml(driver.getLicenseNumber())).append("</td>")
                    .append("<td>").append(escapeHtml(driver.getPhoneNumber())).append("</td>")
                    .append("<td>").append(escapeHtml(driver.getStatus())).append("</td>")
                    .append("</tr>");
        }
        return builder.toString();
    }

    private static String escapeHtml(String value) {
        return value == null ? "" : value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    private static User getAuthenticatedUser(HttpExchange exchange) {
        String sessionId = getSessionId(exchange);
        if (sessionId == null) {
            return null;
        }
        return SESSIONS.get(sessionId);
    }

    private static String getSessionId(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader == null) {
            return null;
        }
        for (String cookie : cookieHeader.split(";")) {
            String[] parts = cookie.trim().split("=", 2);
            if (parts.length == 2 && "transitops_session".equals(parts[0])) {
                return parts[1];
            }
        }
        return null;
    }

    private static void serveFile(HttpExchange exchange, String fileName) throws IOException {
        Path file = WEB_ROOT.resolve(fileName).normalize();
        if (Files.exists(file) && Files.isRegularFile(file)) {
            byte[] data = Files.readAllBytes(file);
            sendBytes(exchange, 200, data, "text/html; charset=UTF-8");
        } else {
            sendText(exchange, 404, "File not found");
        }
    }

    private static Map<String, String> readForm(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> values = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2) {
                values.put(URLDecoder.decode(parts[0], StandardCharsets.UTF_8), URLDecoder.decode(parts[1], StandardCharsets.UTF_8));
            }
        }
        return values;
    }

    private static void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        sendBytes(exchange, statusCode, data, "text/html; charset=UTF-8");
    }

    private static void sendBytes(HttpExchange exchange, int statusCode, byte[] data, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, data.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(data);
        }
    }

    private static class MaintenanceRecord {
        private final String vehicle;
        private final String title;
        private final String date;
        private final String description;
        private final String status;

        private MaintenanceRecord(String vehicle, String title, String date, String description, String status) {
            this.vehicle = vehicle;
            this.title = title;
            this.date = date;
            this.description = description;
            this.status = status;
        }
    }

    private static class TripRecord {
        private final String route;
        private final String vehicle;
        private final String driver;
        private final String status;

        private TripRecord(String route, String vehicle, String driver, String status) {
            this.route = route;
            this.vehicle = vehicle;
            this.driver = driver;
            this.status = status;
        }
    }
}
