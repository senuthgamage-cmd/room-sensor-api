package com.example.roomsensor.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.roomsensor.domain.Room;
import com.example.roomsensor.dto.CreateRoomRequest;
import com.example.roomsensor.dto.UpdateRoomRequest;
import com.example.roomsensor.exception.ConflictException;
import com.example.roomsensor.exception.ForbiddenOperationException;
import com.example.roomsensor.store.InMemoryStore;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private final Map<String, Room> rooms = InMemoryStore.getInstance().rooms();
    private final Map<String, com.example.roomsensor.domain.Sensor> sensors = InMemoryStore.getInstance().sensors();

    @GET
    public List<Room> getAllRooms() {
        synchronized (rooms) {
            return new ArrayList<>(rooms.values());
        }
    }

    @GET
    @Path("/{id}")
    public Room getRoom(@PathParam("id") String id) {
        Room room = rooms.get(id);
        if (room == null) {
            throw new NotFoundException("Room not found: " + id);
        }
        return room;
    }

    @POST
    public Response createRoom(CreateRoomRequest request) {
        String id = request.getId() == null || request.getId().isBlank() ? UUID.randomUUID().toString() : request.getId();
        if (rooms.containsKey(id)) {
            throw new ConflictException("Room already exists: " + id);
        }

        Room room = new Room(id, request.getName(), request.getFloor());
        rooms.put(id, room);

        return Response.created(URI.create("/api/v1/rooms/" + id)).entity(room).build();
    }

    @PUT
    @Path("/{id}")
    public Room updateRoom(@PathParam("id") String id, UpdateRoomRequest request) {
        Room room = rooms.get(id);
        if (room == null) {
            throw new NotFoundException("Room not found: " + id);
        }

        room.setName(request.getName());
        room.setFloor(request.getFloor());
        return room;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        boolean hasSensors;
        synchronized (sensors) {
            hasSensors = sensors.values().stream().anyMatch(sensor -> id.equals(sensor.getRoomId()));
        }

        if (hasSensors) {
            throw new ForbiddenOperationException("Cannot delete room with assigned sensors: " + id);
        }

        Room removed = rooms.remove(id);
        if (removed == null) {
            throw new NotFoundException("Room not found: " + id);
        }

        return Response.noContent().build();
    }
}
