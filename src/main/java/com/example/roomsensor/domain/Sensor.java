package com.example.roomsensor.domain;

import java.util.ArrayList;
import java.util.List;

public class Sensor {
    private String id;
    private String type;
    private String roomId;
    private List<Reading> readings = new ArrayList<>();

    public Sensor() {
    }

    public Sensor(String id, String type, String roomId) {
        this.id = id;
        this.type = type;
        this.roomId = roomId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<Reading> getReadings() {
        return readings;
    }

    public void setReadings(List<Reading> readings) {
        this.readings = readings;
    }
}
