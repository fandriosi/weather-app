package com.andriosi.weather.web.dto;

import java.util.UUID;

public class UnidadeResponse {

    private UUID id;
    private String nome;
    private String simbolo;
    private String parametro;

    public UnidadeResponse(UUID id, String nome, String simbolo, String parametro) {
        this.id = id;
        this.nome = nome;
        this.simbolo = simbolo;
        this.parametro = parametro;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public String getParametro() {
        return parametro;
    }
}
