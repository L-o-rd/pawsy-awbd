package com.awbd.pawsy.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.awbd.pawsy.user.repository.RoleRepository;
import com.awbd.pawsy.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.awbd.pawsy.user.model.User;
import lombok.RequiredArgsConstructor;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void registerUser(User user) {
        log.info("Registering user `{}`.", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var adopterRole = roleRepository.findRoleByName("Adopter").orElseThrow();
        user.setRoles(Set.of(adopterRole));
        userRepository.save(user);
        log.info("New user registered `{}`.", user.getUsername());
    }
}
