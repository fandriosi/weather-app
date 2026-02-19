package com.andriosi.weather.web.dto;

import java.util.UUID;
import com.andriosi.weather.domain.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(
        UUID id,
        @NotBlank
        String username,
        @NotBlank
        String name,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password,
        @NotNull
        RoleName role
        ) {

}
