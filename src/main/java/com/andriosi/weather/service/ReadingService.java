package com.andriosi.weather.service;

import com.andriosi.weather.domain.Reading;
import com.andriosi.weather.domain.Sensor;
import com.andriosi.weather.port.ReadingIngestPort;
import com.andriosi.weather.repository.ReadingRepository;
import com.andriosi.weather.repository.SensorRepository;
import com.andriosi.weather.web.dto.ReadingIngestRequest;
import com.andriosi.weather.web.dto.ReadingResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReadingService implements ReadingIngestPort {

    private final ReadingRepository readingRepository;
    private final SensorRepository sensorRepository;

    public ReadingService(ReadingRepository readingRepository, SensorRepository sensorRepository) {
        this.readingRepository = readingRepository;
        this.sensorRepository = sensorRepository;
    }

    @Override
    @Transactional
    public ReadingResponse ingest(ReadingIngestRequest request) {
        Sensor sensor = sensorRepository.findById(request.getSensorId())
            .orElseThrow(() -> new IllegalArgumentException("Sensor not found"));

        Reading reading = new Reading();
        reading.setSensor(sensor);
        reading.setValue(request.getValue());
        reading.setUnit(request.getUnit());
        reading.setObservedAt(request.getObservedAt() != null ? request.getObservedAt() : Instant.now());
        reading.setCreatedAt(Instant.now());

        Reading saved = readingRepository.save(reading);
        return new ReadingResponse(saved.getId(), sensor.getId(), saved.getValue(), saved.getUnit(), saved.getObservedAt());
    }

    @Transactional(readOnly = true)
    public List<ReadingResponse> listBySensor(Long sensorId) {
        return readingRepository.findBySensorIdOrderByObservedAtDesc(sensorId).stream()
            .map(reading -> new ReadingResponse(
                reading.getId(),
                reading.getSensor().getId(),
                reading.getValue(),
                reading.getUnit(),
                reading.getObservedAt()
            ))
            .toList();
    }
}
