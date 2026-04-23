package com.awbd.pawsy.integration.adoption;

import com.awbd.pawsy.adoption.dto.AppointmentCreateRequest;
import com.awbd.pawsy.adoption.model.AppointmentStatus;
import com.awbd.pawsy.adoption.repository.AdoptionRepository;
import com.awbd.pawsy.adoption.repository.AppointmentRepository;
import com.awbd.pawsy.adoption.service.AppointmentService;
import com.awbd.pawsy.pet.dto.PetCreateRequest;
import com.awbd.pawsy.pet.dto.ShelterCreateRequest;
import com.awbd.pawsy.pet.model.PetStatus;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.pet.service.ShelterService;
import com.awbd.pawsy.user.dto.UserCreateRequest;
import com.awbd.pawsy.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdoptionRequestFlowIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetService petService;

    @Autowired
    private AdoptionRepository adoptionRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ShelterService shelterService;

    @BeforeEach
    public void setup() {
        userService.registerUser(new UserCreateRequest("user", "user", "first", "last", "user@user.mail", "0123456789"));
        userService.registerUser(new UserCreateRequest("manager", "manager", "first", "last", "manager@user.mail", "0123456789"));
        userService.makeManager(userService.getByUsername("manager"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADOPTER"})
    public void whenPetIsAvailable_adopters_canAdoptThePet() throws Exception {
        var user = userService.getByUsername("user");
        log.info("Adopter Id: {}", user.getId());
        var manager = userService.getByUsername("manager");
        log.info("Manager Id: {}", manager.getId());
        shelterService.create(new ShelterCreateRequest("shelter", "location", "shelter@mail.com", "0123456789"), manager);
        var shelter = shelterService.getByManager(manager);
        var petDto = new PetCreateRequest("pet", "species", 3, "description", "Male", null);
        var pet = petService.create(petDto, shelter);

        appointmentService.create("user", pet.getId(), new AppointmentCreateRequest(LocalDate.now().plusDays(3)));
        mockMvc.perform(post("/pets/" + pet.getId() + "/adopt")
                        .param("message", "I would love to adopt this pet!")
                        .with(csrf()))
                        .andExpect(status().is3xxRedirection());

        var adoption = adoptionRepository.findAll().getFirst();
        mockMvc.perform(post("/shelters/adoptions/" + adoption.getId() + "/approve")
                        .with(csrf()))
                        .andExpect(status().isForbidden());

        SecurityContextHolder.clearContext();
        mockMvc.perform(post("/shelters/adoptions/" + adoption.getId() + "/approve")
                        .with(csrf())
                        .with(user("manager").roles("ADOPTER", "MANAGER")))
                        .andExpect(status().is3xxRedirection());

        var updated = petService.get(pet.getId());
        assertEquals(PetStatus.Adopted, updated.getStatus());

        var updatedApps = appointmentRepository.findByPetId(pet.getId());
        assertFalse(updatedApps.isEmpty());
        assertEquals(AppointmentStatus.Cancelled, updatedApps.getFirst().getStatus());
    }
}
