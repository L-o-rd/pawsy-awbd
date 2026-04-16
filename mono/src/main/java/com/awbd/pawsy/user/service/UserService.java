package com.awbd.pawsy.user.service;

import com.awbd.pawsy.user.dto.UserUpdateRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.awbd.pawsy.user.repository.RoleRepository;
import com.awbd.pawsy.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityExistsException;
import com.awbd.pawsy.user.dto.UserCreateRequest;
import org.springframework.stereotype.Service;
import com.awbd.pawsy.user.dto.UserMapper;
import com.awbd.pawsy.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        user.setRoles(Set.of(adopterRole));
        userRepository.save(user);
        log.info("New user registered `{}`.", user.getUsername());
    }

    public User getByUsername(final String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new EntityNotFoundException("User `%s` was not found.".formatted(username)));
    }

    public UserUpdateRequest getProfileForUpdate(User user) {
        return userMapper.toUpdateRequest(user);
    }

    public void update(final String username, UserUpdateRequest dto) {
        var user = getByUsername(username);
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setPhone(dto.phone());
        userRepository.save(user);
    }

    public void makeManager(User user) {
        var roles = user.getRoles();
        roles.add(roleRepository.findRoleByName("ROLE_MANAGER").orElseThrow());
        user.setRoles(roles);
        userRepository.save(user);
    }

    public Long count() {
        return userRepository.count();
    }
}
