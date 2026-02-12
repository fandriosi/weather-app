package com.andriosi.weather.web.dto;

import java.util.UUID;

public class StationResponse {

    private UUID id;
    private String name;
    private Double latitude;
    private Double longitude;

    public StationResponse(UUID id, String name, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
