package com.andriosi.weather.web.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SensorResponse(
        UUID id,
        String name,
        String description,
        SensorTypeResponse type,
        SensorStatus status,
        List<UnidadeResponse> unidades,
        ImageData image,
        List<FileData> files,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

    public record ImageData(
            UUID id,
            String url,
            String originalName,
            String contentType,
            long size,
            LocalDateTime uploadedAt
            ) {

    }

    public record FileData(
            UUID id,
            String url,
            String originalName,
            String contentType,
            long size,
            LocalDateTime uploadedAt
            ) {

    }
}
