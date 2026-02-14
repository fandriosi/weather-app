package com.andriosi.weather.web;

import com.andriosi.weather.port.ReadingIngestPort;
import com.andriosi.weather.web.dto.MqttSimPayload;
import com.andriosi.weather.web.dto.ReadingIngestRequest;
import com.andriosi.weather.web.dto.ReadingResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingest")
public class IngestController {

    private final ReadingIngestPort readingIngestPort;

    public IngestController(ReadingIngestPort readingIngestPort) {
        this.readingIngestPort = readingIngestPort;
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PostMapping("/http")
    public ReadingResponse ingestHttp(@Valid @RequestBody ReadingIngestRequest request) {
        return readingIngestPort.ingest(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PostMapping("/mqtt-sim")
    public ReadingResponse ingestMqttSim(@Valid @RequestBody MqttSimPayload payload) {
        ReadingIngestRequest request = new ReadingIngestRequest();
        request.setStationId(payload.getStationId());
        request.setValue(payload.getValue());
        request.setUnit(payload.getUnit());
        request.setObservedAt(payload.getObservedAt());
        return readingIngestPort.ingest(request);
    }
}
