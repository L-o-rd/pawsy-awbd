package com.awbd.pawsy.integration.appointment;

import com.awbd.pawsy.adoption.model.AppointmentStatus;
import com.awbd.pawsy.adoption.repository.AppointmentRepository;
import com.awbd.pawsy.pet.dto.PetCreateRequest;
import com.awbd.pawsy.pet.dto.ShelterCreateRequest;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

@Slf4j
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AppointmentFlowIT {

    @Autowired
    private PetService petService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        userService.registerUser(new UserCreateRequest("user", "user", "first", "last", "user@user.mail", "0123456789"));
        userService.registerUser(new UserCreateRequest("manager", "manager", "first", "last", "manager@user.mail", "0123456789"));
        userService.makeManager(userService.getByUsername("manager"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADOPTER"})
    void whenUserTriesMultipleTimes_appointments_shouldPreventDuplicates() throws Exception {
        var user = userService.getByUsername("user");
        log.info("Adopter Id: {}", user.getId());
        var manager = userService.getByUsername("manager");
        log.info("Manager Id: {}", manager.getId());
        shelterService.create(new ShelterCreateRequest("shelter", "location", "shelter@mail.com", "0123456789"), manager);
        var shelter = shelterService.getByManager(manager);
        var petDto = new PetCreateRequest("pet", "species", 3, "description", "Male", null);
        var pet = petService.create(petDto, shelter);

        var date = LocalDate.now().plusDays(3).toString();
        mockMvc.perform(post("/pets/" + pet.getId() + "/appointments")
                        .param("appointmentDate", date)
                        .with(csrf()))
                        .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/pets/" + pet.getId() + "/appointments")
                        .param("appointmentDate", date)
                        .with(csrf()))
                        .andExpect(model().attributeExists("errorMessage"));

        var app = appointmentRepository.findAll().getFirst();
        mockMvc.perform(post("/appointments/" + app.getId() + "/cancel")
                        .with(csrf()))
                        .andExpect(status().is3xxRedirection());

        var updated = appointmentRepository.findById(app.getId()).orElseThrow();
        assertEquals(AppointmentStatus.Cancelled, updated.getStatus());
    }
}
