package com.andriosi.weather.web.dto;

import java.util.Set;

public class UserResponse {

    private Long id;
    private String username;
    private boolean enabled;
    private Set<String> roles;

    public UserResponse(Long id, String username, boolean enabled, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.enabled = enabled;
        this.roles = roles;
    }

    public Long getId() {
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
