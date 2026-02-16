package com.andriosi.weather.web;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.andriosi.weather.service.SensorService;
import com.andriosi.weather.web.dto.SensorRequest;
import com.andriosi.weather.web.dto.SensorResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public SensorResponse create(@Valid @RequestBody SensorRequest request) {
        return sensorService.create(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SensorResponse createWithFiles(
            @Valid @RequestPart(value = "data", required = false) SensorRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        SensorRequest resolved = requireRequestPart(request);
        return sensorService.create(resolved, image, files);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SensorResponse update(@PathVariable("id") UUID id,
            @Valid @RequestBody SensorRequest request) {
        return sensorService.update(id, request, null, List.of());
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SensorResponse updateWithFiles(
            @PathVariable("id") UUID id,
            @Valid @RequestPart(value = "data", required = false) SensorRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        SensorRequest resolved = requireRequestPart(request);
        return sensorService.update(id, resolved, image, files);
    } 

    private SensorRequest requireRequestPart(SensorRequest request) {
        if (request != null) {
            return request;
        }
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Missing multipart part 'data'"
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @DeleteMapping(path = "/{sensorId}/files/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable UUID sensorId, @PathVariable UUID fileId) {
        sensorService.deleteFile(sensorId, fileId);
        return ResponseEntity.noContent().build();
    }

}
