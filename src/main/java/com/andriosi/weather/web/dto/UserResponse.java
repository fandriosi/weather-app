package com.andriosi.weather.web.dto;

import java.util.Set;
import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String username;
    private boolean enabled;
    private Set<String> roles;

    public UserResponse(UUID id, String username, boolean enabled, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.enabled = enabled;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
