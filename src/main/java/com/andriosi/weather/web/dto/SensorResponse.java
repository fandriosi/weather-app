package com.andriosi.weather.web.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.andriosi.weather.domain.SensorType;
import com.andriosi.weather.domain.SensorTypeEntity;

public class SensorResponse {

    private UUID id;
    private String name;
    private String description;
    private SensorTypeEntity type;
    private SensorStatus status;
    private List<UnidadeResponse> unidades;
    private ImageData image;
    private List<FileData> files;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor completo
    public SensorResponse(
            UUID id,
            String name,
            String description,
            SensorTypeEntity type,
            SensorStatus status,
            List<UnidadeResponse> unidades,
            ImageData image,
            List<FileData> files,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = status;
        this.image = image;
        this.unidades = unidades;
        this.files = files;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Records aninhados
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

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SensorTypeEntity getType() {
        return type;
    }

    public void setType(SensorTypeEntity type) {
        this.type = type;
    }

    public SensorStatus getStatus() {
        return status;
    }

    public void setStatus(SensorStatus status) {
        this.status = status;
    }

    public ImageData getImage() {
        return image;
    }

    public void setImage(ImageData image) {
        this.image = image;
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void setFiles(List<FileData> files) {
        this.files = files;
    }

    public List<UnidadeResponse> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeResponse> unidades) {
        this.unidades = unidades;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
