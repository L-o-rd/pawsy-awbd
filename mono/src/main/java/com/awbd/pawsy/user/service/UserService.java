package com.awbd.pawsy.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.awbd.pawsy.user.repository.RoleRepository;
import com.awbd.pawsy.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;
import com.awbd.pawsy.user.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void registerUser(User user) {
        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new EntityExistsException("Username already taken.");
        }

        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new EntityExistsException("Email already in use.");
        }

        log.info("Registering user `{}`.", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var adopterRole = roleRepository.findRoleByName("ROLE_ADOPTER").orElseThrow(() -> new EntityNotFoundException("Role `ROLE_ADOPTER` was not found."));
        user.setRoles(Set.of(adopterRole));
        userRepository.save(user);
        log.info("New user registered `{}`.", user.getUsername());
    }

    public User getByUsername(final String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new EntityNotFoundException("User `%s` was not found.".formatted(username)));
    }
}
