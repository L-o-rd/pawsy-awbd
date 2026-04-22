package com.awbd.pawsy.user.service;

import static org.junit.jupiter.api.Assertions.*;

import com.awbd.pawsy.user.dto.UserCreateRequest;
import com.awbd.pawsy.user.dto.UserMapper;
import com.awbd.pawsy.user.dto.UserUpdateRequest;
import com.awbd.pawsy.user.model.Role;
import com.awbd.pawsy.user.model.User;
import com.awbd.pawsy.user.repository.RoleRepository;
import com.awbd.pawsy.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void whenUsernameExists_registerUser_throwsEntityExistsException() {
        var dto = new UserCreateRequest("user", "user", "First", "Last", "user@user.com", "0123456789");
        when(userRepository.existsUserByUsername(dto.username())).thenReturn(true);
        var ex = assertThrows(EntityExistsException.class, () -> userService.registerUser(dto));
        assertEquals("Username already taken.", ex.getMessage());
        verify(userRepository).existsUserByUsername(dto.username());
    }

    @Test
    public void whenEmailExists_registerUser_throwsEntityExistsException() {
        var dto = new UserCreateRequest("user", "user", "First", "Last", "user@user.com", "0123456789");
        when(userRepository.existsUserByUsername(dto.username())).thenReturn(false);
        when(userRepository.existsUserByEmail(dto.email())).thenReturn(true);
        var ex = assertThrows(EntityExistsException.class, () -> userService.registerUser(dto));
        assertEquals("Email already in use.", ex.getMessage());
        verify(userRepository).existsUserByUsername(dto.username());
        verify(userRepository).existsUserByEmail(dto.email());
    }

    @Test
    public void whenDtoIsValid_registerUser_registersANewUser() {
        var dto = new UserCreateRequest("user", "user", "First", "Last", "user@user.com", "0123456789");
        when(userRepository.existsUserByUsername(dto.username())).thenReturn(false);
        when(userRepository.existsUserByEmail(dto.email())).thenReturn(false);
        when(userMapper.toUser(dto)).thenReturn(new User());
        when(passwordEncoder.encode(dto.password())).thenReturn(dto.password());
        when(roleRepository.findRoleByName("ROLE_ADOPTER")).thenReturn(Optional.of(new Role()));
        var savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername(dto.username());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        userService.registerUser(dto);
        verify(userRepository).existsUserByUsername(dto.username());
        verify(userRepository).existsUserByEmail(dto.email());
        verify(userMapper).toUser(dto);
        verify(passwordEncoder).encode(dto.password());
        verify(roleRepository).findRoleByName("ROLE_ADOPTER");
    }

    @Test
    public void whenUserExists_getByUsername_getsUser() {
        final String username = "user";
        final User user = new User();
        user.setId(1L);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        var result = userService.getByUsername(username);
        assertEquals(1L, result.getId());
        verify(userRepository).findUserByUsername(username);
    }

    @Test
    public void always_getProfileForUpdate_mapsToProfile() {
        final User user = new User();
        final UserUpdateRequest dto = new UserUpdateRequest("user", null, null, null, null);
        when(userMapper.toUpdateRequest(user)).thenReturn(dto);
        var result = userService.getProfileForUpdate(user);
        assertEquals(dto.username(), result.username());
        verify(userMapper).toUpdateRequest(user);
    }

    @Test
    public void whenUserExists_update_updatesTheUser() {
        final User user = new User();
        user.setFirstName("first");
        user.setLastName("last");
        user.setPhone("0");
        final var username = "user";
        final UserUpdateRequest dto = new UserUpdateRequest("user", "user@user.com", "last", "first", "1");
        final User savedUser = new User();
        savedUser.setFirstName(dto.firstName());
        savedUser.setLastName(dto.lastName());
        savedUser.setPhone(dto.phone());
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(savedUser);
        userService.update(username, dto);
        assertEquals(dto.firstName(), savedUser.getFirstName());
        verify(userRepository).findUserByUsername(username);
        verify(userRepository).save(user);
    }

    @Test
    public void whenUserExists_makeManager_promotesTheUser() {
        when(roleRepository.findRoleByName("ROLE_MANAGER")).thenReturn(Optional.of(new Role()));
        final User user = new User();
        user.setRoles(new HashSet<>());
        final var savedUser = new User();
        savedUser.setRoles(Set.of(new Role()));
        when(userRepository.save(user)).thenReturn(savedUser);
        userService.makeManager(user);
        assertEquals(1, savedUser.getRoles().size());
        verify(roleRepository).findRoleByName("ROLE_MANAGER");
        verify(userRepository).save(user);
    }

    @Test
    public void always_count_countsUsers() {
        when(userRepository.count()).thenReturn(1L);
        final var count = userService.count();
        assertEquals(1L, count);
        verify(userRepository).count();
    }
}