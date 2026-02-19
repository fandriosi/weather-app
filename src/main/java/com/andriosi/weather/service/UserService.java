package com.andriosi.weather.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andriosi.weather.domain.AppUser;
import com.andriosi.weather.domain.Role;
import com.andriosi.weather.exeception.UserProcessException;
import com.andriosi.weather.repository.RoleRepository;
import com.andriosi.weather.repository.UserRepository;
import com.andriosi.weather.web.dto.UserCreateRequest;
import com.andriosi.weather.web.dto.UserResponse;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserProcessException("Nome de usuário já existe");
        }

        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> new UserProcessException("Função não encontrada"));

        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);

        AppUser saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getUsername(), saved.getRole().getName().name(), user.getEmail(), saved.isEnabled(),
                user.getRole().getName().name());
    }

    @Transactional
    public UserResponse update(UUID id, UserCreateRequest request) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new UserProcessException("Usuário não encontrado"));

        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> new UserProcessException("Função não encontrada"));

        user.setId(id);
        user.setUsername(request.username());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);

        AppUser saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getUsername(), saved.getRole().getName().name(), user.getEmail(), saved.isEnabled(),
                user.getRole().getName().name());
    }

    @Transactional
    public void deleteById(UUID id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new UserProcessException("Usuário não encontrado"));
        if (user.isEnabled()) {
            throw new UserProcessException("Não é possível excluir um usuário habilitado");
        }
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.isEnabled(),
                user.getRole().getName().name()
        ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name());

        return new User(user.getUsername(), user.getPassword(), user.isEnabled(), true, true, true, List.of(authority));
    }
}
