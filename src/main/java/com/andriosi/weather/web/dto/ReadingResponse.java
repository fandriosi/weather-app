package com.andriosi.weather.web.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadingResponse(
        UUID id,
        UUID stationId,
        double value,
        String unit,
        Instant observedAt
        ) {

}
