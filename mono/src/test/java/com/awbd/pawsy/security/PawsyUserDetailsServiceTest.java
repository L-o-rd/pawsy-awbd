package com.awbd.pawsy.security;

import com.awbd.pawsy.user.model.User;
import com.awbd.pawsy.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PawsyUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PawsyUserDetailsService pawsyUserDetailsService;

    @Test
    public void whenUserExists_loadUserByUsername_loadsTheUser() {
        final var username = "user";
        final var user = new User();
        user.setUsername(username);
        user.setId(1L);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        var userDetails = pawsyUserDetailsService.loadUserByUsername(username);
        assertEquals(user.getUsername(), userDetails.getUsername());
        verify(userRepository).findUserByUsername(username);
    }
}