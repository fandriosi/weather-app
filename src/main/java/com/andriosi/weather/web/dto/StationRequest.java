package com.andriosi.weather.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StationRequest(
        @NotBlank
        String name,
        @NotNull
        Double latitude,
        @NotNull
        Double longitude,
        @NotNull
        String sensorId
        ) {

}
