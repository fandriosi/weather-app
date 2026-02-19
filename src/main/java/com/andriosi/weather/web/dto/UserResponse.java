package com.andriosi.weather.web.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String name,
        String email,
        boolean enabled,
        String role
        ) {

}
