package com.andriosi.weather.repository;

import com.andriosi.weather.domain.Sensor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {

    @Query("SELECT DISTINCT s FROM Sensor s JOIN FETCH s.type LEFT JOIN FETCH s.unidades")
    List<Sensor> findAllWithType();

    @Query("SELECT s FROM Sensor s JOIN FETCH s.type LEFT JOIN FETCH s.unidades WHERE s.id = :id")
    Sensor findByIdWithType(UUID id);

    @Query("SELECT s FROM Sensor s JOIN FETCH s.type LEFT JOIN FETCH s.unidades")
    List<Sensor> findAllWithTypeAndUnidades();
}
