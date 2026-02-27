package com.andriosi.weather.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.andriosi.weather.domain.Sensor;
import com.andriosi.weather.domain.SensorFile;
import com.andriosi.weather.domain.SensorTypeEntity;
import com.andriosi.weather.domain.Unidade;
import com.andriosi.weather.dto.SensorDTO;
import com.andriosi.weather.exeception.ResourceNotFoundException;
import com.andriosi.weather.repository.SensorFileRepository;
import com.andriosi.weather.repository.SensorRepository;
import com.andriosi.weather.repository.SensorTypeRepository;
import com.andriosi.weather.repository.UnidadeRepository;
import com.andriosi.weather.service.mapper.SensorFileMapper;
import com.andriosi.weather.storage.FileStorageService;
import com.andriosi.weather.storage.StoredFileInfo;
import com.andriosi.weather.web.dto.SensorFileResponse;
import com.andriosi.weather.web.dto.SensorRequest;
import com.andriosi.weather.web.dto.SensorResponse;
import com.andriosi.weather.web.dto.SensorTypeResponse;
import com.andriosi.weather.web.dto.UnidadeResponse;

@Service
public class SensorService {

    @Autowired
    private ModelMapper modelMapper;

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

    public SensorDTO convertToDto(Sensor sensor) {
        return modelMapper.map(sensor, SensorDTO.class);
    }

    public Sensor convertToEntity(SensorDTO sensorDTO) {
        return modelMapper.map(sensorDTO, Sensor.class);
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
                .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado"));

        applyRequest(sensor, request);

        Sensor saved = sensorRepository.save(sensor);

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
        entity.setCreatedAt(LocalDateTime.now());
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

    private List<UnidadeResponse> toUnidadeResponses(List<Unidade> unidades) {
        return unidades.stream()
                .map(unidade -> new UnidadeResponse(
                unidade.getId(),
                unidade.getNome(),
                unidade.getSimbolo()
        ))
                .toList();
    }

    private void applyUnidades(SensorRequest request, Sensor sensor) {
        List<UUID> unidadeIds = request.getUnidadeIds();
        if (unidadeIds == null || unidadeIds.isEmpty()) {
            return;
        }

        List<Unidade> unidades = unidadeRepository.findAllById(unidadeIds);
        if (unidades.size() != unidadeIds.size()) {
            throw new ResourceNotFoundException("Uma ou mais unidades não encontradas");
        }
        sensor.setUnidades(unidades);
    }

    private SensorResponse toResponse(Sensor sensor) {
        Sensor sensorReflesh = sensorRepository.findByIdWithType(sensor.getId());
        if (sensorReflesh == null) {
            throw new ResourceNotFoundException("Sensor não encontrado");
        }

        List<SensorFile> allFiles = sensorFileRepository.findBySensorId(sensorReflesh.getId());

        var partition = allFiles.stream()
                .collect(Collectors.partitioningBy(SensorFile::getIsImage));

        Optional<SensorFile> imageFile = partition.get(true).stream().findFirst();
        List<SensorFile> regularFiles = partition.get(false);

        SensorResponse.ImageData imageData = imageFile
                .map(file -> fileMapper.toImageData(file, sensorReflesh.getId()))
                .orElse(null);

        List<SensorResponse.FileData> filesData = fileMapper.toFileDataList(
                regularFiles,
                sensorReflesh.getId()
        );

        return new SensorResponse(
                sensorReflesh.getId(),
                sensorReflesh.getName(),
                sensorReflesh.getDescription(),
                getTypeResponse(sensorReflesh),
                sensorReflesh.getStatus(),
                sensorReflesh.getUnidades().stream()
                        .map(unidade -> new UnidadeResponse(
                        unidade.getId(),
                        unidade.getNome(),
                        unidade.getSimbolo()
                )).toList(),
                imageData,
                filesData,
                sensorReflesh.getCreatedAt(),
                sensorReflesh.getUpdatedAt()
        );
    }

    private SensorTypeResponse getTypeResponse(Sensor sensor) {
        // Conversão de SensorTypeEntity para SensorTypeResponse
        SensorTypeResponse typeResponse = null;
        if (sensor.getType() != null) {
            typeResponse = new SensorTypeResponse(
                    sensor.getType().getId(),
                    sensor.getType().getName()
            );
        }
        return typeResponse;
    }

    private void applyRequest(Sensor sensor, SensorRequest request) {
        sensor.setName(request.getName());
        sensor.setDescription(request.getDescription());
        sensor.setType(resolveSensorType(request.getTypeId()));
        sensor.setStatus(request.getStatus());
        if (sensor.getCreatedAt() != null) {
            sensor.setUpdatedAt(LocalDateTime.now());
        } else {
            sensor.setCreatedAt(LocalDateTime.now());
        }
        applyUnidades(request, sensor);
    }

    private SensorTypeEntity resolveSensorType(UUID type) {
        if (type == null) {
            return null;
        }
        return sensorTypeRepository.findById(type)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de sensor não encontrado: " + type));
    }

    private SensorFileResponse toFileResponse(SensorFile file) {
        return new SensorFileResponse(
                file.getId(),
                file.getOriginalName(),
                file.getContentType(),
                file.getSize(),
                file.getStorageType(),
                file.getStorageKey(),
                file.getStorageUrl(),
                file.getIsImage(),
                file.getCreatedAt()
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
                .orElseThrow(() -> new com.andriosi.weather.exeception.ResourceNotFoundException(
                String.format("Arquivo %s não encontrado para o sensor %s", fileId, sensorId)
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
                .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado"));
        return toResponse(sensor);
    }

    public void deleteSensor(UUID sensorId) {
        deleteAllFiles(sensorId);
        sensorRepository.delete(
                sensorRepository.findById(sensorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Sensor não encontrado"))
        );
    }

    public Resource getFileResource(UUID sensorId, UUID fileId) {
        SensorFile file = sensorFileRepository.findByIdAndSensorId(fileId, sensorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Arquivo %s não encontrado para o sensor %s", fileId, sensorId)
        ));

        return fileStorageService.getFile(file.getStorageKey(), file.getStorageType());
    }

    public SensorFileResponse getFile(UUID sensorId, UUID fileId) {
        SensorFile file = sensorFileRepository.findByIdAndSensorId(fileId, sensorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Arquivo %s não encontrado para o sensor %s", fileId, sensorId)
        ));
        return toFileResponse(file);
    }

    public List<SensorResponse> getAll() {
        List<Sensor> sensors = sensorRepository.findAllWithTypeAndUnidades();
        Map<UUID, List<SensorFileResponse>> filesBySensorId = loadFilesBySensorId(sensors);

        return sensors.stream()
                .map(sensor -> {
                    List<SensorFileResponse> files = filesBySensorId.getOrDefault(sensor.getId(), List.of());
                    var partition = files.stream()
                            .collect(Collectors.partitioningBy(SensorFileResponse::isImage));

                    Optional<SensorFileResponse> imageFile = partition.get(true).stream().findFirst();
                    List<SensorFileResponse> regularFiles = partition.get(false);

                    SensorResponse.ImageData imageData = imageFile
                            .map(file -> new SensorResponse.ImageData(
                            file.id(),
                            file.storageUrl(),
                            file.originalName(),
                            file.contentType(),
                            file.size(),
                            file.createdAt() // Use getCreatedAt() for image
                    ))
                            .orElse(null);

                    List<SensorResponse.FileData> filesData = regularFiles.stream()
                            .map(file -> new SensorResponse.FileData(
                            file.id(),
                            file.storageUrl(),
                            file.originalName(),
                            file.contentType(),
                            file.size(),
                            file.createdAt() // Use getCreatedAt() for file as well
                    ))
                            .toList();

                    return new SensorResponse(
                            sensor.getId(),
                            sensor.getName(),
                            sensor.getDescription(),
                            getTypeResponse(sensor),
                            sensor.getStatus(),
                            toUnidadeResponses(sensor.getUnidades()),
                            imageData,
                            filesData,
                            sensor.getCreatedAt(),
                            sensor.getUpdatedAt()
                    );
                }).toList();
    }

}
