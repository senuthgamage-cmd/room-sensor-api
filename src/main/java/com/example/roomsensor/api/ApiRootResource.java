package com.example.roomsensor.api;

import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
public class ApiRootResource {
    @GET
    public Map<String, Object> root() {
        return Map.of(
                "name", "room-sensor-api",
                "status", "ok",
                "endpoints", new String[] { "/api/v1/rooms", "/api/v1/sensors" });
    }
}
