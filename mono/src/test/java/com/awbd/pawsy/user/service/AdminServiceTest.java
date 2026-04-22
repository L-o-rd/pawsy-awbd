package com.awbd.pawsy.user.service;

import com.awbd.pawsy.adoption.model.AdoptionStatus;
import com.awbd.pawsy.adoption.model.AppointmentStatus;
import com.awbd.pawsy.adoption.service.AdoptionService;
import com.awbd.pawsy.adoption.service.AppointmentService;
import com.awbd.pawsy.pet.model.PetStatus;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.pet.service.ShelterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private AdoptionService adoptionService;

    @Mock
    private ShelterService shelterService;

    @Mock
    private UserService userService;

    @Mock
    private PetService petService;

    @InjectMocks
    private AdminService adminService;

    @Test
    void always_getStats_returnsAStatsDto() {
        when(petService.count()).thenReturn(1L);
        when(petService.countByStatus(any(PetStatus.class))).thenReturn(2L);
        when(userService.count()).thenReturn(3L);
        when(shelterService.count()).thenReturn(4L);
        when(adoptionService.count()).thenReturn(5L);
        when(adoptionService.countByStatus(any(AdoptionStatus.class))).thenReturn(6L);
        when(appointmentService.count()).thenReturn(7L);
        when(appointmentService.countByStatus(any(AppointmentStatus.class))).thenReturn(8L);
        var dto = adminService.getStats();
        assertEquals(1L, dto.totalPets());
        assertEquals(2L, dto.availablePets());
        assertEquals(2L, dto.adoptedPets());
        assertEquals(3L, dto.totalUsers());
        assertEquals(4L, dto.totalShelters());
        assertEquals(5L, dto.totalAdoptions());
        assertEquals(6L, dto.pendingAdoptions());
        assertEquals(7L, dto.totalAppointments());
        assertEquals(8L, dto.ongoingAppointments());

        verify(petService).count();
        verify(petService, times(2)).countByStatus(any(PetStatus.class));
        verify(userService).count();
        verify(shelterService).count();
        verify(adoptionService).count();
        verify(adoptionService, times(1)).countByStatus(any(AdoptionStatus.class));
        verify(appointmentService).count();
        verify(appointmentService, times(1)).countByStatus(any(AppointmentStatus.class));
    }
}