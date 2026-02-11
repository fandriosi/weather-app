package com.andriosi.weather.web.dto;

import com.andriosi.weather.domain.SensorType;

public class SensorResponse {

    private Long id;
    private String name;
    private SensorType type;
    private Long stationId;

    public SensorResponse(Long id, String name, SensorType type, Long stationId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.stationId = stationId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SensorType getType() {
        return type;
    }

    public Long getStationId() {
        return stationId;
    }
}
