package com.andriosi.weather.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andriosi.weather.domain.Station;
import com.andriosi.weather.repository.StationRepository;
import com.andriosi.weather.domain.Sensor;
import com.andriosi.weather.repository.SensorRepository;
import com.andriosi.weather.web.dto.SensorSResponse;
import com.andriosi.weather.web.dto.StationRequest;
import com.andriosi.weather.web.dto.StationResponse;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final SensorRepository sensorRepository;

    public StationService(StationRepository stationRepository, SensorRepository sensorRepository) {
        this.stationRepository = stationRepository;
        this.sensorRepository = sensorRepository;
    }

    @Transactional
    public StationResponse create(StationRequest request) {
        Station station = new Station();
        station.setName(request.name());
        station.setLatitude(request.latitude());
        station.setLongitude(request.longitude());
        Sensor sensor = sensorRepository.findById(UUID.fromString(request.sensorId()))
                .orElseThrow(() -> new RuntimeException("Sensor não encontrado"));
        station.setSensor(sensor);
        Station saved = stationRepository.save(station);
        return new StationResponse(saved.getId(), saved.getName(), saved.getLatitude(), saved.getLongitude(),
                saved.getSensor() != null ? new SensorSResponse(saved.getSensor().getId(),
                saved.getSensor().getName()) : null);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> list() {
        return stationRepository.findAll().stream()
                .map(station -> new StationResponse(station.getId(), station.getName(),
                station.getLatitude(), station.getLongitude(),
                station.getSensor() != null
                ? new SensorSResponse(station.getSensor().getId(), station.getSensor().getName()) : null))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<StationResponse> getById(UUID id) {
        return stationRepository.findById(id)
                .map(station -> new StationResponse(station.getId(), station.getName(),
                station.getLatitude(), station.getLongitude(), station.getSensor() != null
                ? new SensorSResponse(station.getSensor().getId(), station.getSensor().getName()) : null));
    }

    @Transactional
    public Optional<StationResponse> update(UUID id, StationRequest request) {
        return stationRepository.findById(id).map(station -> {
            station.setName(request.name());
            station.setLatitude(request.latitude());
            station.setLongitude(request.longitude());
            Sensor sensor = sensorRepository.findById(UUID.fromString(request.sensorId()))
                    .orElseThrow(() -> new RuntimeException("Sensor não encontrado"));
            station.setSensor(sensor);
            Station updated = stationRepository.save(station);
            return new StationResponse(updated.getId(), updated.getName(), updated.getLatitude(),
                    updated.getLongitude(), updated.getSensor() != null
                    ? new SensorSResponse(updated.getSensor().getId(), updated.getSensor().getName()) : null);
        });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (stationRepository.existsById(id)) {
            stationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
