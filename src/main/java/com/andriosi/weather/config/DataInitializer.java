package com.andriosi.weather.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.andriosi.weather.domain.AppUser;
import com.andriosi.weather.domain.Role;
import com.andriosi.weather.domain.RoleName;
import com.andriosi.weather.repository.RoleRepository;
import com.andriosi.weather.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRolesAndAdmin(RoleRepository roleRepository, UserRepository userRepository,
                                        PasswordEncoder encoder) {
        return args -> {
            for (RoleName roleName : RoleName.values()) {
                roleRepository.findByName(roleName).orElseGet(() -> roleRepository.save(new Role(roleName)));
            }

            if (!userRepository.existsByUsername("admin")) {
                Role adminRole = roleRepository.findByName(RoleName.ADMIN).orElseThrow();
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setName("Admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(adminRole);
                userRepository.save(admin);
            }
        };
    }
}
