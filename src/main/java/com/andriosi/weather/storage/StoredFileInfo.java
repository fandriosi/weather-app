package com.andriosi.weather.storage;

public class StoredFileInfo {

    private final StorageType storageType;
    private final String storageKey;
    private final String storageUrl;
    private final String originalName;
    private final String contentType;
    private final long size;

    public StoredFileInfo(StorageType storageType,
                          String storageKey,
                          String storageUrl,
                          String originalName,
                          String contentType,
                          long size) {
        this.storageType = storageType;
        this.storageKey = storageKey;
        this.storageUrl = storageUrl;
        this.originalName = originalName;
        this.contentType = contentType;
        this.size = size;
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

    public String getOriginalName() {
        return originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }
}
