package com.andriosi.weather.web.dto;

import jakarta.validation.constraints.NotBlank;

public class UnidadeRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String simbolo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }
}
