package com.example.roomsensor.api;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.roomsensor.domain.Reading;
import com.example.roomsensor.domain.Sensor;
import com.example.roomsensor.dto.CreateReadingRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingsResource {
    private final Sensor sensor;

    public SensorReadingsResource(Sensor sensor) {
        this.sensor = sensor;
    }

    @GET
    public List<Reading> getReadings() {
        return new ArrayList<>(sensor.getReadings());
    }

    @POST
    public Response addReading(CreateReadingRequest request) {
        String id = request.getId() == null || request.getId().isBlank() ? UUID.randomUUID().toString() : request.getId();
        Reading reading = new Reading(id, request.getValue(), Instant.now());
        sensor.getReadings().add(reading);

        return Response.created(URI.create("/api/v1/sensors/" + sensor.getId() + "/readings/" + id))
                .entity(reading)
                .build();
    }
}
