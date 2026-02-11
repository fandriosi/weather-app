package com.andriosi.weather.repository;

import com.andriosi.weather.domain.Sensor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByStationId(Long stationId);
}
