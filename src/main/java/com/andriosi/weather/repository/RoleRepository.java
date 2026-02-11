package com.andriosi.weather.repository;

import com.andriosi.weather.domain.Role;
import com.andriosi.weather.domain.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
