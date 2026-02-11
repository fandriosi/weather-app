package com.andriosi.weather.web.dto;

import java.time.Instant;

public class ReadingResponse {

    private Long id;
    private Long sensorId;
    private double value;
    private String unit;
    private Instant observedAt;

    public ReadingResponse(Long id, Long sensorId, double value, String unit, Instant observedAt) {
        this.id = id;
        this.sensorId = sensorId;
        this.value = value;
        this.unit = unit;
        this.observedAt = observedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getSensorId() {
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
