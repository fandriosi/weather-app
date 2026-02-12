package com.andriosi.weather.web.dto;

public class AuthUserResponse {

    private String id;
    private String username;
    private String name;
    private String email;
    private String role;

    public AuthUserResponse(String id, String username, String name, String email, String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
