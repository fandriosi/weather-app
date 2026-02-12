package com.andriosi.weather.service;

import com.andriosi.weather.domain.Sensor;
import com.andriosi.weather.domain.Station;
import com.andriosi.weather.domain.Unidade;
import com.andriosi.weather.repository.SensorRepository;
import com.andriosi.weather.repository.StationRepository;
import com.andriosi.weather.repository.UnidadeRepository;
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
    private final UnidadeRepository unidadeRepository;

    public SensorService(SensorRepository sensorRepository,
                         StationRepository stationRepository,
                         UnidadeRepository unidadeRepository) {
        this.sensorRepository = sensorRepository;
        this.stationRepository = stationRepository;
        this.unidadeRepository = unidadeRepository;
    }

    @Transactional
    public SensorResponse create(SensorRequest request) {
        List<Station> stations = stationRepository.findAllById(request.getStationIds());
        if (stations.size() != request.getStationIds().size()) {
            throw new IllegalArgumentException("One or more stations not found");
        }

        Sensor sensor = new Sensor();
        sensor.setName(request.getName());
        sensor.setType(request.getType());
        sensor.setStations(stations);
        applyUnidades(request, sensor);

        Sensor saved = sensorRepository.save(sensor);
        return new SensorResponse(
            saved.getId(),
            saved.getName(),
            saved.getType(),
            toStationIds(saved),
            toUnidadeIds(saved)
        );
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
                toStationIds(sensor),
                toUnidadeIds(sensor)
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
            throw new IllegalArgumentException("One or more unidades not found");
        }
        sensor.setUnidades(unidades);
    }

    private List<UUID> toUnidadeIds(Sensor sensor) {
        return sensor.getUnidades().stream()
            .map(Unidade::getId)
            .toList();
    }

    private List<UUID> toStationIds(Sensor sensor) {
        return sensor.getStations().stream()
            .map(Station::getId)
            .toList();
    }
}
