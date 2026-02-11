package com.andriosi.weather.web.dto;

public class StationResponse {

    private Long id;
    private String name;
    private String location;

    public StationResponse(Long id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
