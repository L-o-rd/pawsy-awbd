package com.awbd.pawsy.security;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.context.annotation.Profile;
import com.awbd.pawsy.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.jspecify.annotations.NullMarked;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@Service
@Profile({"dev", "test"})
@RequiredArgsConstructor
public class PawsyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User `%s` was not found.".formatted(username)));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getCredentialsNonExpired(),
                user.getAccountNonLocked(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toSet()));
    }
}
