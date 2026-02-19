package com.andriosi.weather.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record MqttSimPayload(
        @NotBlank
        String topic,
        @NotNull
        Double value,
        @NotNull
        UUID stationId,
        @NotBlank
        String unit,
        Instant observedAt
) {}