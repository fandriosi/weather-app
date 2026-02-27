package com.andriosi.weather.web.dto;

import java.util.UUID;

public record UnidadeResponse(
        UUID id,
        String nome,
        String simbolo
        ) {

}