package com.andriosi.weather.web;

import com.andriosi.weather.service.SensorService;
import com.andriosi.weather.web.dto.SensorRequest;
import com.andriosi.weather.web.dto.SensorResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PostMapping
    public SensorResponse create(@Valid @RequestBody SensorRequest request) {
        return sensorService.create(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','READER')")
    @GetMapping
    public List<SensorResponse> list(@RequestParam(name = "stationId", required = false) Long stationId) {
        return sensorService.list(stationId);
    }
}
