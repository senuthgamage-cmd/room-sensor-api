package com.example.roomsensor.domain;

import java.time.Instant;

public class Reading {
    private String id;
    private Double value;
    private Instant timestamp;

    public Reading() {
    }

    public Reading(String id, Double value, Instant timestamp) {
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
