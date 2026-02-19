package com.andriosi.weather.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UnidadeRequest(
        @NotBlank
        String nome,
        @NotBlank
        String simbolo
        ) {

}
