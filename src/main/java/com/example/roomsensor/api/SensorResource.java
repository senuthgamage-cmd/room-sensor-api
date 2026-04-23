package com.example.roomsensor.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.roomsensor.domain.Sensor;
import com.example.roomsensor.dto.CreateSensorRequest;
import com.example.roomsensor.exception.ConflictException;
import com.example.roomsensor.exception.UnprocessableEntityException;
import com.example.roomsensor.store.InMemoryStore;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private final Map<String, Sensor> sensors = InMemoryStore.getInstance().sensors();
    private final Map<String, com.example.roomsensor.domain.Room> rooms = InMemoryStore.getInstance().rooms();

    @POST
    public Response createSensor(CreateSensorRequest request) {
        if (request.getRoomId() == null || !rooms.containsKey(request.getRoomId())) {
            throw new UnprocessableEntityException("Invalid roomId: " + request.getRoomId());
        }

        String id = request.getId() == null || request.getId().isBlank() ? UUID.randomUUID().toString() : request.getId();
        if (sensors.containsKey(id)) {
            throw new ConflictException("Sensor already exists: " + id);
        }

        Sensor sensor = new Sensor(id, request.getType(), request.getRoomId());
        sensors.put(id, sensor);

        return Response.created(URI.create("/api/v1/sensors/" + id)).entity(sensor).build();
    }

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        synchronized (sensors) {
            if (type == null || type.isBlank()) {
                return new ArrayList<>(sensors.values());
            }
            return sensors.values().stream()
                    .filter(sensor -> type.equalsIgnoreCase(sensor.getType()))
                    .collect(Collectors.toList());
        }
    }

    @GET
    @Path("/{id}")
    public Sensor getSensor(@PathParam("id") String id) {
        Sensor sensor = sensors.get(id);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found: " + id);
        }
        return sensor;
    }

    @Path("/{id}/readings")
    public SensorReadingsResource readings(@PathParam("id") String id) {
        Sensor sensor = sensors.get(id);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found: " + id);
        }
        return new SensorReadingsResource(sensor);
    }
}
