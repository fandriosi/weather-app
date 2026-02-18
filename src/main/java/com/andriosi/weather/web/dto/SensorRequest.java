package com.andriosi.weather.web.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SensorRequest {

    @NotBlank
    private String name;

    @NotNull
    private UUID typeId;

    private String description;
    
    @NotNull
    private SensorStatus status;
    
    @NotNull
    private List<UUID> unidadeIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getTypeId() {
        return typeId;
    }

    public void setTypeId(UUID typeId) {
        this.typeId = typeId;
    }
    
    public List<UUID> getUnidadeIds() {
        return unidadeIds;
    }

    public void setUnidadeIds(List<UUID> unidadeIds) {
        this.unidadeIds = unidadeIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SensorStatus getStatus() {
        return status;
    }

    public void setStatus(SensorStatus status) {
        this.status = status;
    }

    
}
