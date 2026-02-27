package com.andriosi.weather.web.dto;

import java.util.UUID;

public record StationResponse(
        UUID id,
        String name,
        Double latitude,
        Double longitude,
        SensorSResponse sensor
        ) {

}
