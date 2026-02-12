package com.andriosi.weather.web.dto;

import java.time.Instant;

public class AuthResponse {

    private String token;
    private AuthUserResponse user;
    private Instant expiresAt;

    public AuthResponse(String token, AuthUserResponse user, Instant expiresAt) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public AuthUserResponse getUser() {
        return user;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
