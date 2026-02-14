package com.andriosi.weather.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andriosi.weather.domain.SensorFile;

public interface SensorFileRepository extends JpaRepository<SensorFile, UUID> {
    List<SensorFile> findBySensorId(UUID sensorId);

    List<SensorFile> findBySensorIdIn(List<UUID> sensorIds);

    Optional<SensorFile> findByIdAndSensorId(UUID id, UUID sensorId);
}
