package com.awbd.pawsy.integration.review;

import com.awbd.pawsy.pet.dto.ShelterCreateRequest;
import com.awbd.pawsy.pet.repository.ReviewRepository;
import com.awbd.pawsy.pet.service.ShelterService;
import com.awbd.pawsy.user.dto.UserCreateRequest;
import com.awbd.pawsy.user.repository.RoleRepository;
import com.awbd.pawsy.user.repository.UserRepository;
import com.awbd.pawsy.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReviewFlowIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setup() {
        userService.registerUser(new UserCreateRequest("user", "user", "first", "last", "user@user.mail", "0123456789"));
        userService.registerUser(new UserCreateRequest("manager", "manager", "first", "last", "manager@user.mail", "0123456789"));
        userService.makeManager(userService.getByUsername("manager"));
        userService.registerUser(new UserCreateRequest("admin", "admin", "first", "last", "admin@user.mail", "0123456789"));
        var user = userService.getByUsername("admin");
        user.getRoles().add(roleRepository.findRoleByName("ROLE_ADMIN").orElseThrow());
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADOPTER"})
    void reviewLifecycle_shouldWorkCorrectly() throws Exception {
        var user = userService.getByUsername("user");
        log.info("Adopter Id: {}", user.getId());
        var manager = userService.getByUsername("manager");
        log.info("Manager Id: {}", manager.getId());
        shelterService.create(new ShelterCreateRequest("shelter", "location", "shelter@mail.com", "0123456789"), manager);
        var shelter = shelterService.getByManager(manager);

        mockMvc.perform(post("/shelters/" + shelter.getId() + "/review")
                        .param("comment", "Amazing shelter experience!")
                        .param("rating", "5")
                        .with(csrf()))
                        .andExpect(status().is3xxRedirection());

        assertEquals(1, reviewRepository.count());
        mockMvc.perform(post("/shelters/" + shelter.getId() + "/review")
                        .param("comment", "Updated message text!")
                        .param("rating", "4")
                        .with(csrf()))
                        .andExpect(status().is3xxRedirection());

        assertEquals(1, reviewRepository.count());
        var reviewId = reviewRepository.findAll().getFirst().getId();
        mockMvc.perform(post("/admin/reviews/" + reviewId + "/delete")
                        .with(csrf())
                        .with(user("admin").roles("ADOPTER", "ADMIN")))
                        .andExpect(status().is3xxRedirection());

        assertEquals(0, reviewRepository.count());
    }
}
