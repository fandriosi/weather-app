package com.andriosi.weather.repository;

import com.andriosi.weather.domain.Sensor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {    
}
