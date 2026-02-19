package com.andriosi.weather.web.dto;

public record AuthUserResponse(
        String id,
        String username,
        String name,
        String email,
        String role
        ) {

}
