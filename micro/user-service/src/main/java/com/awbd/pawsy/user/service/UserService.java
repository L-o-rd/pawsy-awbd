package com.awbd.pawsy.user.service;

import com.awbd.pawsy.user.dto.UserCreateRequest;
import com.awbd.pawsy.user.dto.UserMapper;
import com.awbd.pawsy.user.dto.UserSummary;
import com.awbd.pawsy.user.model.User;
import com.awbd.pawsy.user.repository.RoleRepository;
import com.awbd.pawsy.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public void registerUser(UserCreateRequest dto) {
        if (userRepository.existsUserByUsername(dto.username())) {
            throw new EntityExistsException("Username already taken.");
        }

        if (userRepository.existsUserByEmail(dto.email())) {
            throw new EntityExistsException("Email already in use.");
        }

        var user = userMapper.toUser(dto);
        log.info("Registering user `{}`.", dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        var adopterRole = roleRepository.findRoleByName("ROLE_ADOPTER").orElseThrow(() -> new EntityNotFoundException("Role `ROLE_ADOPTER` was not found."));
        user.setRoles(new HashSet<>(Set.of(adopterRole)));
        userRepository.save(user);
        log.info("New user registered `{}`.", user.getUsername());
    }

    public Optional<User> getByUsername(final String username) {
        return userRepository.findUserByUsername(username);
    }

    public UserSummary summary(User user) {
        return userMapper.toSummary(user);
    }
}
