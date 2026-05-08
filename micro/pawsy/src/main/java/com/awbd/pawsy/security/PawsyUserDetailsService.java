package com.awbd.pawsy.security;

import com.awbd.pawsy.client.UserClient;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.jspecify.annotations.NullMarked;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PawsyUserDetailsService implements UserDetailsService {
    private final UserClient userClient;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userClient.getByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User `%s` does not exist.".formatted(username)));
        return new org.springframework.security.core.userdetails.User(
                user.username(),
                user.password(),
                user.enabled(),
                user.accountNonExpired(),
                user.credentialsNonExpired(),
                user.accountNonLocked(),
                user.roles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toSet()));
    }
}