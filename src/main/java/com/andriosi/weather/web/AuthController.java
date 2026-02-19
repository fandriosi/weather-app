package com.andriosi.weather.web;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andriosi.weather.domain.AppUser;
import com.andriosi.weather.repository.UserRepository;
import com.andriosi.weather.security.JwtService;
import com.andriosi.weather.web.dto.AuthRequest;
import com.andriosi.weather.web.dto.AuthResponse;
import com.andriosi.weather.web.dto.AuthUserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
            UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AppUser user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        String token = jwtService.generateToken(userDetails);
        Instant expiresAt = jwtService.getExpiration(token);
        String role = user.getRole().getName().name().toLowerCase();

        AuthUserResponse payload = new AuthUserResponse(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                role
        );

        return new AuthResponse(token, payload, expiresAt);
    }
}
