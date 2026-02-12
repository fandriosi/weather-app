package com.andriosi.weather.web.dto;

import java.time.Instant;
import java.util.UUID;

public class ReadingResponse {

    private UUID id;
    private UUID sensorId;
    private double value;
    private String unit;
    private Instant observedAt;

    public ReadingResponse(UUID id, UUID sensorId, double value, String unit, Instant observedAt) {
        this.id = id;
        this.sensorId = sensorId;
        this.value = value;
        this.unit = unit;
        this.observedAt = observedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSensorId() {
        return sensorId;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public Instant getObservedAt() {
        return observedAt;
    }
}
