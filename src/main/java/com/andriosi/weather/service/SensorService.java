package com.andriosi.weather.service;

import com.andriosi.weather.domain.Sensor;
import com.andriosi.weather.domain.SensorFile;
import com.andriosi.weather.domain.SensorType;
import com.andriosi.weather.domain.SensorTypeEntity;
import com.andriosi.weather.domain.Unidade;
import com.andriosi.weather.repository.SensorRepository;
import com.andriosi.weather.repository.SensorFileRepository;
import com.andriosi.weather.repository.SensorTypeRepository;
import com.andriosi.weather.repository.UnidadeRepository;
import com.andriosi.weather.service.mapper.SensorFileMapper;
import com.andriosi.weather.storage.FileStorageService;
import com.andriosi.weather.storage.StoredFileInfo;
import com.andriosi.weather.web.dto.SensorRequest;
import com.andriosi.weather.web.dto.SensorFileResponse;
import com.andriosi.weather.web.dto.SensorResponse;
import org.springframework.core.io.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final SensorTypeRepository sensorTypeRepository;

    public SensorService(
            SensorRepository sensorRepository,
            SensorFileRepository sensorFileRepository,
            FileStorageService fileStorageService,
            SensorFileMapper fileMapper,
            UnidadeRepository unidadeRepository,
            SensorTypeRepository sensorTypeRepository) {
        this.sensorRepository = sensorRepository;
        this.sensorFileRepository = sensorFileRepository;
        this.fileStorageService = fileStorageService;
        this.fileMapper = fileMapper;
        this.unidadeRepository = unidadeRepository;
        this.sensorTypeRepository = sensorTypeRepository;
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
    public SensorResponse update(UUID sensorId, SensorRequest request, MultipartFile image, List<MultipartFile> files) {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found"));

        applyRequest(sensor, request);

        Sensor saved = sensorRepository.save(sensor);

        // Deleta arquivos existentes se há novos arquivos
        if ((image != null && !image.isEmpty()) || (files != null && !files.isEmpty())) {
            deleteAllFiles(sensorId);
        }

        saveFiles(saved, image, files);
        return toResponse(saved);
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

    private SensorResponse toResponse(Sensor sensor) {
        List<SensorFile> allFiles = sensorFileRepository.findBySensorId(sensor.getId());

        var partition = allFiles.stream()
                .collect(Collectors.partitioningBy(SensorFile::getIsImage));

        Optional<SensorFile> imageFile = partition.get(true).stream().findFirst();
        List<SensorFile> regularFiles = partition.get(false);

        SensorResponse.ImageData imageData = imageFile
                .map(file -> fileMapper.toImageData(file, sensor.getId()))
                .orElse(null);

        List<SensorResponse.FileData> filesData = fileMapper.toFileDataList(
                regularFiles,
                sensor.getId()
        );

        return new SensorResponse(
                sensor.getId(),
                sensor.getDescription(),
                toSensorType(sensor.getType()),
                sensor.getStatus(),
                toUnidadeIds(sensor),
                imageData,
                filesData,
                toLocalDateTime(sensor.getCreatedAt()),
                toLocalDateTime(sensor.getUpdatedAt())
        );

    }

    private LocalDateTime toLocalDateTime(LocalDateTime createdAt) {
        if (createdAt == null) {
            return null;
        }
        return createdAt;
    }

    private void applyRequest(Sensor sensor, SensorRequest request) {
        sensor.setName(request.getName());
        sensor.setDescription(request.getDescription());
        sensor.setType(resolveSensorType(request.getType()));
        sensor.setStatus(request.getStatus());
        applyUnidades(request, sensor);
    }

    private SensorTypeEntity resolveSensorType(SensorType type) {
        if (type == null) {
            return null;
        }
        return sensorTypeRepository.findByName(type.name())
                .orElseThrow(() -> new IllegalArgumentException("SensorType not found: " + type.name()));
    }

    private SensorType toSensorType(SensorTypeEntity entity) {
        if (entity == null || entity.getName() == null) {
            return null;
        }
        return SensorType.valueOf(entity.getName());
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

    private void saveFiles(Sensor sensor, MultipartFile image, List<MultipartFile> files) {
        List<SensorFile> entities = new ArrayList<>();

        // Processa imagem
        if (image != null && !image.isEmpty()) {
            List<StoredFileInfo> imageStored = fileStorageService.storeSensorFiles(sensor.getId(), List.of(image));
            if (!imageStored.isEmpty()) {
                entities.add(toEntity(sensor, imageStored.get(0), true));
            } else {
                log.warn("Failed to store image for sensor {}: {}", sensor.getId(), image.getOriginalFilename());
            }
        }

        // Processa arquivos
        if (files != null && !files.isEmpty()) {
            List<StoredFileInfo> storedFiles = fileStorageService.storeSensorFiles(sensor.getId(), files);
            for (StoredFileInfo storedFile : storedFiles) {
                entities.add(toEntity(sensor, storedFile, false));
            }
        }

        if (!entities.isEmpty()) {
            sensorFileRepository.saveAll(entities);
        }
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

        sensorFileRepository.deleteById(fileId);
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
        List<UUID> ids = files.stream()
                .map(SensorFile::getId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());

        sensorFileRepository.deleteAllById(ids);
        log.info("Deleted {} files for sensor {}", files.size(), sensorId);
    }

    //Metédo que retorna o repoonse do sensor, com os arquivos associados, para ser usado no download do arquivo
    public SensorResponse getSensorWithFiles(UUID sensorId) {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found"));
        return toResponse(sensor);
    }

    public void deleteSensor(UUID sensorId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteSensor'");
    }
}
