package com.andriosi.weather.web.dto;

import java.util.List;
import java.util.UUID;

import com.andriosi.weather.domain.SensorType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SensorRequest {

    @NotBlank
    private String name;

    @NotNull
    private SensorType type;

    private List<UUID> unidadeIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SensorType getType() {
        return type;
    }

    public void setType(SensorType type) {
        this.type = type;
    }
    
    public List<UUID> getUnidadeIds() {
        return unidadeIds;
    }

    public void setUnidadeIds(List<UUID> unidadeIds) {
        this.unidadeIds = unidadeIds;
    }
}
