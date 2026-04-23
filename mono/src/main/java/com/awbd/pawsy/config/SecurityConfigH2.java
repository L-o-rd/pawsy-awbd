package com.awbd.pawsy.config;

import com.awbd.pawsy.security.PawsyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("test")
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfigH2 {
    private final PawsyUserDetailsService pawsyUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/", "/login", "/register",
                        "/css/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/pets/create/**").hasRole("MANAGER")
                .requestMatchers("/shelters/pets/**", "/shelters/adoptions/**").hasRole("MANAGER")
                .requestMatchers("/pets/*/edit", "/pets/*/delete").hasRole("MANAGER")
                .requestMatchers("/pets", "/shelters", "/about").permitAll()
                .anyRequest().authenticated())
                .userDetailsService(pawsyUserDetailsService)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**"))
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/"));

        return http.build();
    }
}
