package com.andriosi.weather.service;

import com.andriosi.weather.domain.Sensor;
import com.andriosi.weather.domain.SensorFile;
import com.andriosi.weather.domain.Unidade;
import com.andriosi.weather.exeception.ResourceNotFoundException;
import com.andriosi.weather.repository.SensorRepository;
import com.andriosi.weather.repository.SensorFileRepository;
import com.andriosi.weather.repository.UnidadeRepository;
import com.andriosi.weather.service.mapper.SensorFileMapper;
import com.andriosi.weather.storage.FileStorageService;
import com.andriosi.weather.storage.StoredFileInfo;
import com.andriosi.weather.web.dto.SensorRequest;
import com.andriosi.weather.web.dto.SensorFileResponse;
import com.andriosi.weather.web.dto.SensorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SensorService {

    private static final Logger log = LoggerFactory.getLogger(SensorService.class);
    private final SensorRepository sensorRepository;
    private final SensorFileRepository sensorFileRepository;
    private final FileStorageService fileStorageService;
    private final SensorFileMapper fileMapper;
    private final UnidadeRepository unidadeRepository;

    public SensorService(
            SensorRepository sensorRepository,
            SensorFileRepository sensorFileRepository,
            FileStorageService fileStorageService,
            SensorFileMapper fileMapper,
            UnidadeRepository unidadeRepository) {
        this.sensorRepository = sensorRepository;
        this.sensorFileRepository = sensorFileRepository;
        this.fileStorageService = fileStorageService;
        this.fileMapper = fileMapper;
        this.unidadeRepository = unidadeRepository;
    }

    @Transactional
    public SensorResponse create(SensorRequest request) {
        return create(request, null, List.of());
    }

    @Transactional
    public SensorResponse create(SensorRequest request, List<MultipartFile> files) {
        return create(request, null, files);
    }

    @Transactional
    public SensorResponse create(SensorRequest request, MultipartFile image, List<MultipartFile> files) {
        Sensor sensor = new Sensor();
        applyRequest(sensor, request);
        Sensor saved = sensorRepository.save(sensor);

        saveFiles(saved, image, files);

        return toResponse(saved);
    }

    @Transactional
    public SensorResponse update(UUID sensorId, SensorRequest request, List<MultipartFile> files) {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found"));

        sensor.setName(request.getName());
        sensor.setType(request.getType());
        applyUnidades(request, sensor);

        Sensor saved = sensorRepository.save(sensor);
        saveFiles(saved, null, files);
        List<SensorFileResponse> allFiles = toFileResponses(sensorFileRepository.findBySensorId(saved.getId()));
        return toResponse(saved, allFiles);
    }

    

    private SensorFile toEntity(Sensor sensor, StoredFileInfo info, boolean isImage) {
        SensorFile entity = new SensorFile();
        entity.setSensor(sensor);
        entity.setOriginalName(info.getOriginalName());
        entity.setContentType(info.getContentType());
        entity.setSize(info.getSize());
        entity.setStorageType(info.getStorageType());
        entity.setStorageKey(info.getStorageKey());
        entity.setStorageUrl(info.getStorageUrl());
        entity.setCreatedAt(Instant.now());
        entity.setIsImage(isImage);
        return entity;
    }

    private Map<UUID, List<SensorFileResponse>> loadFilesBySensorId(List<Sensor> sensors) {
        List<UUID> sensorIds = sensors.stream()
                .map(Sensor::getId)
                .toList();
        if (sensorIds.isEmpty()) {
            return Map.of();
        }

        List<SensorFile> files = sensorFileRepository.findBySensorIdIn(sensorIds);
        Map<UUID, List<SensorFileResponse>> result = new HashMap<>();
        for (SensorFile file : files) {
            UUID sensorId = file.getSensor().getId();
            result.computeIfAbsent(sensorId, key -> new ArrayList<>()).add(toFileResponse(file));
        }
        return result;
    }

    private void applyUnidades(SensorRequest request, Sensor sensor) {
        List<UUID> unidadeIds = request.getUnidadeIds();
        if (unidadeIds == null || unidadeIds.isEmpty()) {
            return;
        }

        List<Unidade> unidades = unidadeRepository.findAllById(unidadeIds);
        if (unidades.size() != unidadeIds.size()) {
            throw new IllegalArgumentException("One or more unidades not found");
        }
        sensor.setUnidades(unidades);
    }

    private List<UUID> toUnidadeIds(Sensor sensor) {
        return sensor.getUnidades().stream()
                .map(Unidade::getId)
                .toList();
    }

    private SensorResponse toResponse(Sensor sensor, List<SensorFileResponse> files) {
        return new SensorResponse(
                sensor.getId(),
                sensor.getName(),
                sensor.getType(),
                toUnidadeIds(sensor),
                files
        );
    }

    private void applyRequest(Sensor sensor, SensorRequest request) {
        sensor.setCode(request.getCode());
        sensor.setDescription(request.getDescription());
        sensor.setType(request.getType());
        sensor.setStatus(request.getStatus());
        // ...outros campos
    }

    private List<SensorFileResponse> toFileResponses(List<SensorFile> files) {
        return files.stream()
                .map(this::toFileResponse)
                .toList();
    }

    private SensorFileResponse toFileResponse(SensorFile file) {
        return new SensorFileResponse(
                file.getId(),
                file.getOriginalName(),
                file.getContentType(),
                file.getSize(),
                file.getStorageType(),
                file.getStorageKey(),
                file.getStorageUrl()
        );
    }

    @Transactional
    public void deleteFile(UUID sensorId, UUID fileId) {
        SensorFile file = sensorFileRepository.findByIdAndSensorId(fileId, sensorId)
                .orElseThrow(() -> new com.andriosi.weather.exeception.SensorFileNotFoundException(
                String.format("File %s not found for sensor %s", fileId, sensorId)
        ));

        try {
            fileStorageService.deleteFile(file.getStorageKey(), file.getStorageType());
            log.info("File deleted from storage: {} (sensor: {})", fileId, sensorId);
        } catch (Exception e) {
            log.error("Error deleting file from storage: {} (sensor: {})", fileId, sensorId, e);
        }

        sensorFileRepository.delete(file);
        log.info("File record deleted: {} (sensor: {})", fileId, sensorId);
    }

    @Transactional
    public void deleteAllFiles(UUID sensorId) {
        List<SensorFile> files = sensorFileRepository.findBySensorId(sensorId);

        for (SensorFile file : files) {
            try {
                fileStorageService.deleteFile(file.getStorageKey(), file.getStorageType());
            } catch (Exception e) {
                log.error("Error deleting file {} from storage", file.getId(), e);
            }
        }

        sensorFileRepository.deleteAll(files);
        log.info("Deleted {} files for sensor {}", files.size(), sensorId);
    }

}
