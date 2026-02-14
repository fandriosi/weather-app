package com.andriosi.weather.web.dto;

import java.util.List;
import java.util.UUID;

import com.andriosi.weather.domain.SensorType;

public class SensorResponse {

    private UUID id;
    private String name;
    private SensorType type;
    private List<UUID> unidadeIds;
    private List<SensorFileResponse> files;

    public SensorResponse(UUID id,
                          String name,
                          SensorType type,
                          List<UUID> unidadeIds,
                          List<SensorFileResponse> files) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.unidadeIds = unidadeIds;
        this.files = files;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SensorType getType() {
        return type;
    }

    public List<UUID> getUnidadeIds() {
        return unidadeIds;
    }

    public List<SensorFileResponse> getFiles() {
        return files;
    }
}
