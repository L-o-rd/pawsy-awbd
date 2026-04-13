package com.awbd.pawsy.config;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Configuration;
import com.awbd.pawsy.security.PawsyUserDetailsService;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;
import lombok.RequiredArgsConstructor;

@Configuration
@Profile("dev")
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final PawsyUserDetailsService pawsyUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register",
                        "/css/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/pets/create/**").hasRole("MANAGER")
                .requestMatchers("/shelters/pets/**").hasRole("MANAGER")
                .requestMatchers("/pets/*/edit").hasRole("MANAGER")
                .requestMatchers("/pets/*/delete").hasRole("MANAGER")
                .anyRequest().authenticated())
                .userDetailsService(pawsyUserDetailsService)
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
