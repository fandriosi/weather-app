package com.andriosi.weather.web;

import com.andriosi.weather.service.UnidadeService;
import com.andriosi.weather.web.dto.UnidadeRequest;
import com.andriosi.weather.web.dto.UnidadeResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/unidades")
public class UnidadeController {

    private final UnidadeService unidadeService;

    public UnidadeController(UnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PostMapping
    public UnidadeResponse create(@Valid @RequestBody UnidadeRequest request) {
        return unidadeService.create(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','READER')")
    @GetMapping
    public Page<UnidadeResponse> list(
        @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return unidadeService.list(pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','READER')")
    @GetMapping("/{id}")
    public UnidadeResponse get(@PathVariable UUID id) {
        return unidadeService.get(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PutMapping("/{id}")
    public UnidadeResponse update(@PathVariable UUID id, @Valid @RequestBody UnidadeRequest request) {
        return unidadeService.update(id, request);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        unidadeService.delete(id);
    }
}
