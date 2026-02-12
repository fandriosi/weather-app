package com.andriosi.weather.web.dto;

import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String username;
    private boolean enabled;
    private String role;
    private String email;
    private String name;
    public UserResponse(UUID id, String username, String name,
        String email, boolean enabled, String role) {
        this.username = username;
        this.enabled = enabled;
        this.role = role;
        this.email = email;
        this.name = name;
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

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}
 