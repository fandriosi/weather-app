package com.andriosi.weather.service;

import com.andriosi.weather.domain.Unidade;
import com.andriosi.weather.repository.UnidadeRepository;
import com.andriosi.weather.web.dto.UnidadeRequest;
import com.andriosi.weather.web.dto.UnidadeResponse;
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;

    public UnidadeService(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    @Transactional
    public UnidadeResponse create(UnidadeRequest request) {
        Unidade unidade = new Unidade();
        unidade.setNome(request.nome());
        unidade.setSimbolo(request.simbolo());
        unidade.setParametro(buildParametro(request.nome(), request.simbolo()));
        Unidade saved = unidadeRepository.save(unidade);
        return new UnidadeResponse(saved.getId(), saved.getNome(), saved.getSimbolo()); 
    }

    @Transactional(readOnly = true)
    public Page<UnidadeResponse> list(Pageable pageable) {
        return unidadeRepository.findAll(pageable)
            .map(unidade -> new UnidadeResponse(
                unidade.getId(),
                unidade.getNome(), 
                unidade.getSimbolo()
            ));
    }

    @Transactional(readOnly = true)
    public UnidadeResponse get(UUID id) {
        Unidade unidade = unidadeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Unidade not found"));
        return new UnidadeResponse(
            unidade.getId(),
            unidade.getNome(),
            unidade.getSimbolo()
        );
    }

    @Transactional
    public UnidadeResponse update(UUID id, UnidadeRequest request) {
        Unidade unidade = unidadeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Unidade not found"));
        unidade.setNome(request.nome());
        unidade.setSimbolo(request.simbolo());
        unidade.setParametro(buildParametro(request.nome(), request.simbolo()));
        Unidade saved = unidadeRepository.save(unidade);
        return new UnidadeResponse(saved.getId(), saved.getNome(), saved.getSimbolo());
    }

    @Transactional
    public void delete(UUID id) {
        if (!unidadeRepository.existsById(id)) {
            throw new IllegalArgumentException("Unidade not found");
        }
        unidadeRepository.deleteById(id);
    }

    private String buildParametro(String nome, String simbolo) {
        if (nome == null && simbolo == null) {
            return null;
        }
        String base = ((nome == null ? "" : nome) + " " + (simbolo == null ? "" : simbolo)).trim();
        String normalized = Normalizer.normalize(base, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
        String underscored = normalized.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "_")
            .replaceAll("^_+|_+$", "");
        return underscored;
    }
}
