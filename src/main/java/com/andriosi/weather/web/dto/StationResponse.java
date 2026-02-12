import java.util.UUID;
package com.andriosi.weather.web.dto;

public class StationResponse {

    private UUID id;
    private String name;
    private String location;

    public StationResponse(UUID id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
