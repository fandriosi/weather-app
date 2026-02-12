package com.andriosi.weather.web.dto;

import java.util.List;
import java.util.UUID;

import com.andriosi.weather.domain.SensorType;

public class SensorResponse {

    private UUID id;
    private String name;
    private SensorType type;
    private List<UUID> stationIds;
    private List<UUID> unidadeIds;

    public SensorResponse(UUID id, String name, SensorType type, List<UUID> stationIds, List<UUID> unidadeIds) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.stationIds = stationIds;
        this.unidadeIds = unidadeIds;
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

    public List<UUID> getStationIds() {
        return stationIds;
    }

    public List<UUID> getUnidadeIds() {
        return unidadeIds;
    }
}
