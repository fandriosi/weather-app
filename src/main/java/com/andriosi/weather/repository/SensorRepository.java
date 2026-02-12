package com.andriosi.weather.repository;

import com.andriosi.weather.domain.Sensor;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {
    @Query("SELECT s FROM Sensor s JOIN s.stations st WHERE st.id = :stationId")
    List<Sensor> findByStationId(@Param("stationId") UUID stationId);
}
