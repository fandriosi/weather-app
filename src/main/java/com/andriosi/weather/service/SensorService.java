package com.andriosi.weather.service;

import com.andriosi.weather.domain.Sensor;
import com.andriosi.weather.domain.SensorFile;
import com.andriosi.weather.domain.Unidade;
import com.andriosi.weather.repository.SensorRepository;
import com.andriosi.weather.repository.SensorFileRepository;
import com.andriosi.weather.repository.UnidadeRepository;
import com.andriosi.weather.storage.FileStorageService;
import com.andriosi.weather.storage.StoredFileInfo;
import com.andriosi.weather.web.dto.SensorRequest;
import com.andriosi.weather.web.dto.SensorFileResponse;
import com.andriosi.weather.web.dto.SensorResponse;
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

    private final SensorRepository sensorRepository;
    private final UnidadeRepository unidadeRepository;
    private final SensorFileRepository sensorFileRepository;
    private final FileStorageService fileStorageService;

    public SensorService(SensorRepository sensorRepository,
                         UnidadeRepository unidadeRepository,
                         SensorFileRepository sensorFileRepository,
                         FileStorageService fileStorageService) {
        this.sensorRepository = sensorRepository;
        this.unidadeRepository = unidadeRepository;
        this.sensorFileRepository = sensorFileRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public SensorResponse create(SensorRequest request) {
        return create(request, List.of());
    }

    @Transactional
    public SensorResponse create(SensorRequest request, List<MultipartFile> files) {
        Sensor sensor = new Sensor();
        sensor.setName(request.getName());
        sensor.setType(request.getType());
        applyUnidades(request, sensor);
        
        Sensor saved = sensorRepository.save(sensor);
        List<SensorFileResponse> fileResponses = saveFiles(saved, files);
        return toResponse(saved, fileResponses);
    }

    @Transactional
    public SensorResponse update(UUID sensorId, SensorRequest request, List<MultipartFile> files) {
        Sensor sensor = sensorRepository.findById(sensorId)
            .orElseThrow(() -> new IllegalArgumentException("Sensor not found"));

        sensor.setName(request.getName());
        sensor.setType(request.getType());
        applyUnidades(request, sensor);

        Sensor saved = sensorRepository.save(sensor);
        saveFiles(saved, files);
        List<SensorFileResponse> allFiles = toFileResponses(sensorFileRepository.findBySensorId(saved.getId()));
        return toResponse(saved, allFiles);
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> list(UUID stationId) {
        List<Sensor> sensors = sensorRepository.findAll();
        Map<UUID, List<SensorFileResponse>> filesBySensorId = loadFilesBySensorId(sensors);
        return sensors.stream()
            .map(sensor -> new SensorResponse(
                sensor.getId(),
                sensor.getName(),
                sensor.getType(),
                toUnidadeIds(sensor),
                filesBySensorId.getOrDefault(sensor.getId(), List.of())
            ))
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<SensorFile> findFile(UUID sensorId, UUID fileId) {
        return sensorFileRepository.findByIdAndSensorId(fileId, sensorId);
    }

    private List<SensorFileResponse> saveFiles(Sensor sensor, List<MultipartFile> files) {
        List<StoredFileInfo> storedFiles = fileStorageService.storeSensorFiles(sensor.getId(), files);
        if (storedFiles.isEmpty()) {
            return List.of();
        }

        List<SensorFile> entities = new ArrayList<>();
        for (StoredFileInfo storedFile : storedFiles) {
            SensorFile entity = new SensorFile();
            entity.setSensor(sensor);
            entity.setOriginalName(storedFile.getOriginalName());
            entity.setContentType(storedFile.getContentType());
            entity.setSize(storedFile.getSize());
            entity.setStorageType(storedFile.getStorageType());
            entity.setStorageKey(storedFile.getStorageKey());
            entity.setStorageUrl(storedFile.getStorageUrl());
            entity.setCreatedAt(Instant.now());
            entities.add(entity);
        }

        List<SensorFile> saved = sensorFileRepository.saveAll(entities);
        return toFileResponses(saved);
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

}
