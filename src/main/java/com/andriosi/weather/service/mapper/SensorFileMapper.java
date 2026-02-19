package com.andriosi.weather.service.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.andriosi.weather.domain.SensorFile;
import com.andriosi.weather.web.dto.SensorResponse;

@Component
public class SensorFileMapper {

    public SensorResponse.ImageData toImageData(SensorFile file, UUID sensorId) {
        if (file == null) {
            return null;
        }
        return new SensorResponse.ImageData(
                file.getId(),
                buildFileUrl(sensorId, file.getId()),
                file.getOriginalName(),
                file.getContentType(),
                file.getSize(),
                file.getCreatedAt()
        );
    }

    public SensorResponse.FileData toFileData(SensorFile file, UUID sensorId) {
        return new SensorResponse.FileData(
                file.getId(),
                buildFileUrl(sensorId, file.getId()), file.getOriginalName(),
                file.getContentType(),
                file.getSize(),
                file.getCreatedAt()
        );
    }

    public List<SensorResponse.FileData> toFileDataList(List<SensorFile> files, UUID sensorId) {
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
}
