package com.andriosi.weather.web.dto;

import com.andriosi.weather.storage.StorageType;
import java.time.LocalDateTime;
import java.util.UUID;

public record SensorFileResponse(
        UUID id,
        String originalName,
        String contentType,
        long size,
        StorageType storageType,
        String storageKey,
        String storageUrl,
        boolean isImage,
        LocalDateTime createdAt
        ) {

}
