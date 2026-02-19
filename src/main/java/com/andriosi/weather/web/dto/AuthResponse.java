package com.andriosi.weather.web.dto;

import java.time.Instant;

public record AuthResponse(
        String token,
        AuthUserResponse user,
        Instant expiresAt
        ) {

}
