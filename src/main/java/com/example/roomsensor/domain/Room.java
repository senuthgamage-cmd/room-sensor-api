package com.example.roomsensor.domain;

public class Room {
    private String id;
    private String name;
    private String floor;

    public Room() {
    }

    public Room(String id, String name, String floor) {
        this.id = id;
        this.name = name;
        this.floor = floor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }
}
