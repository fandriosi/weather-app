package com.andriosi.weather.service;

import com.andriosi.weather.domain.Sensor;
import com.andriosi.weather.domain.Station;
import com.andriosi.weather.repository.SensorRepository;
import com.andriosi.weather.repository.StationRepository;
import com.andriosi.weather.web.dto.SensorRequest;
import com.andriosi.weather.web.dto.SensorResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final StationRepository stationRepository;

    public SensorService(SensorRepository sensorRepository, StationRepository stationRepository) {
        this.sensorRepository = sensorRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public SensorResponse create(SensorRequest request) {
        Station station = stationRepository.findById(request.getStationId())
            .orElseThrow(() -> new IllegalArgumentException("Station not found"));

        Sensor sensor = new Sensor();
        sensor.setName(request.getName());
        sensor.setType(request.getType());
        sensor.setStation(station);

        Sensor saved = sensorRepository.save(sensor);
        return new SensorResponse(saved.getId(), saved.getName(), saved.getType(), station.getId());
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> list(UUID stationId) {
        List<Sensor> sensors = stationId == null
            ? sensorRepository.findAll()
            : sensorRepository.findByStationId(stationId);

        return sensors.stream()
            .map(sensor -> new SensorResponse(
                sensor.getId(),
                sensor.getName(),
                sensor.getType(),
                sensor.getStation().getId()
            ))
            .toList();
    }
}
