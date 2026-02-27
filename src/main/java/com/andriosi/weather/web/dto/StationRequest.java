package com.andriosi.weather.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StationRequest(
        UUID id,
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
