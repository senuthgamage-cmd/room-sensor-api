package com.example.roomsensor.store;

import java.util.HashMap;
import java.util.Map;

import com.example.roomsensor.domain.Room;
import com.example.roomsensor.domain.Sensor;

public final class InMemoryStore {
    private static final InMemoryStore INSTANCE = new InMemoryStore();

    private final Map<String, Room> rooms = java.util.Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Sensor> sensors = java.util.Collections.synchronizedMap(new HashMap<>());

    private InMemoryStore() {
    }

    public static InMemoryStore getInstance() {
        return INSTANCE;
    }

    public Map<String, Room> rooms() {
        return rooms;
    }

    public Map<String, Sensor> sensors() {
        return sensors;
    }
}
