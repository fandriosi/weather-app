package com.andriosi.weather.web.dto;

import java.time.Instant;
import java.util.UUID;

public class ReadingResponse {

    private UUID id;
    private UUID stationId;
    private double value;
    private String unit;
    private Instant observedAt;

    public ReadingResponse(UUID id, UUID stationId, double value, String unit, Instant observedAt) {
        this.id = id;
        this.stationId = stationId;
        this.value = value;
        this.unit = unit;
        this.observedAt = observedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getStationId() {
        return stationId;
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
