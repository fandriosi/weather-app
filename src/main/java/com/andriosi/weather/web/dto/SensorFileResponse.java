package com.andriosi.weather.web.dto;

import com.andriosi.weather.storage.StorageType;
import java.util.UUID;

public class SensorFileResponse {

    private UUID id;
    private String originalName;
    private String contentType;
    private long size;
    private StorageType storageType;
    private String storageKey;
    private String storageUrl;

    public SensorFileResponse(UUID id,
                              String originalName,
                              String contentType,
                              long size,
                              StorageType storageType,
                              String storageKey,
                              String storageUrl) {
        this.id = id;
        this.originalName = originalName;
        this.contentType = contentType;
        this.size = size;
        this.storageType = storageType;
        this.storageKey = storageKey;
        this.storageUrl = storageUrl;
    }

    public UUID getId() {
        return id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public String getStorageUrl() {
        return storageUrl;
    }
}
