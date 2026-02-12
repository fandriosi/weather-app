package com.andriosi.weather.web.dto;

import com.andriosi.weather.domain.SensorType;
import java.util.UUID;

public class SensorResponse {

    private UUID id;
    private String name;
    private SensorType type;
    private UUID stationId;

    public SensorResponse(UUID id, String name, SensorType type, UUID stationId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.stationId = stationId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SensorType getType() {
        return type;
    }

    public UUID getStationId() {
        return stationId;
    }
}
