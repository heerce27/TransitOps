package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;

import model.Vehicle;
import service.VehicleService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * REST-style controller for vehicle management.
 *
 * GET    /vehicles           -> list all vehicles
 * GET    /vehicles?id=5      -> single vehicle
 * POST   /vehicles           -> create vehicle (JSON body)
 * PUT    /vehicles?id=5      -> update vehicle (JSON body)
 * DELETE /vehicles?id=5      -> delete vehicle
 */
@WebServlet("/vehicles")
public class VehicleController extends HttpServlet {

    private final VehicleService vehicleService = new VehicleService();

    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)
            (src, type, ctx) -> new JsonPrimitive(src.toString()))
        .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
            (json, type, ctx) -> LocalDate.parse(json.getAsString()))
        .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJson(resp);
        String idParam = req.getParameter("id");
        try {
            if (idParam == null) {
                writeJson(resp, gson.toJson(vehicleService.getAllVehicles()));
            } else {
                Vehicle vehicle = vehicleService.getVehicleById(Integer.parseInt(idParam));
                if (vehicle == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writeJson(resp, errorJson("Vehicle not found."));
                } else {
                    writeJson(resp, gson.toJson(vehicle));
                }
            }
        } catch (SQLException e) {
            serverError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJson(resp);
        try {
            Vehicle vehicle = gson.fromJson(readBody(req), Vehicle.class);
            int newId = vehicleService.createVehicle(vehicle);
            vehicle.setVehicleId(newId);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            writeJson(resp, gson.toJson(vehicle));
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, errorJson(e.getMessage()));
        } catch (SQLException e) {
            serverError(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJson(resp);
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, errorJson("Missing vehicle id."));
            return;
        }
        try {
            Vehicle vehicle = gson.fromJson(readBody(req), Vehicle.class);
            vehicle.setVehicleId(Integer.parseInt(idParam));
            boolean updated = vehicleService.updateVehicle(vehicle);
            if (updated) {
                writeJson(resp, gson.toJson(vehicle));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writeJson(resp, errorJson("Vehicle not found."));
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, errorJson(e.getMessage()));
        } catch (SQLException e) {
            serverError(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setJson(resp);
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, errorJson("Missing vehicle id."));
            return;
        }
        try {
            boolean deleted = vehicleService.deleteVehicle(Integer.parseInt(idParam));
            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writeJson(resp, errorJson("Vehicle not found."));
            }
        } catch (SQLException e) {
            serverError(resp, e);
        }
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private void setJson(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private void writeJson(HttpServletResponse resp, String json) throws IOException {
        resp.getWriter().write(json);
    }

    private String errorJson(String message) {
        return gson.toJson(new ErrorResponse(message));
    }

    private void serverError(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        writeJson(resp, errorJson("Server error: " + e.getMessage()));
    }

    private static class ErrorResponse {
        String error;
        ErrorResponse(String error) { this.error = error; }
    }
}
