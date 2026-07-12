package com.transitops;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.transitops.model.User;
import com.transitops.service.AuthService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransitOpsServer {
    private static final int PORT = 8080;
    private static final Path WEB_ROOT = Paths.get("WebContent").toAbsolutePath().normalize();
    private static final Map<String, User> SESSIONS = new HashMap<>();
    private static final AuthService AUTH_SERVICE = new AuthService();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new StaticFileHandler("index.html"));
        server.createContext("/login", new LoginHandler());
        server.createContext("/dashboard", new DashboardHandler());
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
            Path file = WEB_ROOT.resolve(fileName).normalize();
            if (Files.exists(file)) {
                byte[] content = Files.readAllBytes(file);
                sendBytes(exchange, 200, content, "text/html; charset=UTF-8");
            } else {
                sendText(exchange, 404, "File not found");
            }
        }
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
            String email = form.get("email");
            String password = form.get("password");
            User user = AUTH_SERVICE.authenticate(email, password);

            if (user == null) {
                String html = "<html><body><h2>Login failed</h2><p>Invalid email or password.</p><a href=\"/login\">Try again</a></body></html>";
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
        if (Files.exists(file)) {
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
}
