package com.andriosi.weather.repository;

import com.andriosi.weather.domain.Unidade;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadeRepository extends JpaRepository<Unidade, UUID> {
    java.util.Optional<Unidade> findByParametro(String parametro);
}
