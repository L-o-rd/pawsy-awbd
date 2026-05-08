package com.awbd.pawsy.user;

import com.awbd.pawsy.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements ApplicationRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        roleRepository.checkedInsert("ROLE_ADOPTER");
        roleRepository.checkedInsert("ROLE_MANAGER");
        roleRepository.checkedInsert("ROLE_ADMIN");
    }
}