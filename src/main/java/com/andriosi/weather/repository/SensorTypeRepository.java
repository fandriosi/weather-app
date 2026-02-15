package com.andriosi.weather.repository;

import com.andriosi.weather.domain.SensorTypeEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorTypeRepository extends JpaRepository<SensorTypeEntity, UUID> {

    Optional<SensorTypeEntity> findByName(String name);
}
