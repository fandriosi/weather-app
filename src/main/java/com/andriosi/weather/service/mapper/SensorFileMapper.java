package com.andriosi.weather.service.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.andriosi.weather.domain.SensorFile;
import com.andriosi.weather.web.dto.SensorResponse.FileData;
import com.andriosi.weather.web.dto.SensorResponse.ImageData;

@Component
public class SensorFileMapper {

    public ImageData toImageData(SensorFile file, UUID sensorId) {
        if (file == null) {
            return null;
        }
        return new ImageData(
            file.getId(),
            buildFileUrl(sensorId, file.getId()),
            file.getOriginalName(),
            file.getContentType(),
            file.getSize(),
            toLocalDateTime(file.getCreatedAt())
        );
    }

    public FileData toFileData(SensorFile file, UUID sensorId) {
        return new FileData(
            file.getId(),
            buildFileUrl(sensorId, file.getId()),
            file.getOriginalName(),
            file.getContentType(),
            file.getSize(),
            toLocalDateTime(file.getCreatedAt())
        );
    }

    public List<FileData> toFileDataList(List<SensorFile> files, UUID sensorId) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        return files.stream()
            .map(file -> toFileData(file, sensorId))
            .toList();
    }

    private String buildFileUrl(UUID sensorId, UUID fileId) {
        return String.format("/api/sensors/%s/files/%s", sensorId, fileId);
    }

    private LocalDateTime toLocalDateTime(java.time.Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}