package com.andriosi.weather.web.dto;

import com.andriosi.weather.storage.StorageType;

public record StoredFileInfoResponse(
        StorageType storageType,
        String storageKey,
        String storageUrl,
        String originalName,
        String contentType,
        long size
        ) {

}
