package com.awbd.pawsy.adoption.service;

import com.awbd.pawsy.adoption.dto.AdoptionCreateRequest;
import com.awbd.pawsy.adoption.dto.AdoptionMapper;
import com.awbd.pawsy.adoption.model.Adoption;
import com.awbd.pawsy.adoption.model.AdoptionStatus;
import com.awbd.pawsy.adoption.repository.AdoptionRepository;
import com.awbd.pawsy.pet.model.Pet;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.user.model.User;
import com.awbd.pawsy.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdoptionServiceTest {

    @Mock
    private AdoptionRepository adoptionRepository;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private AdoptionMapper adoptionMapper;

    @Mock
    private UserService userService;

    @Mock
    private PetService petService;

    @InjectMocks
    private AdoptionService adoptionService;

    @Test
    public void whenRequestExists_create_throwsIllegalStateException() {
        var user = new User();
        user.setId(1L);
        when(userService.getByUsername("user")).thenReturn(user);
        when(petService.get(1L)).thenReturn(new Pet());
        when(adoptionRepository.existsByAdopterIdAndPetId(1L, 1L)).thenReturn(true);
        var ex = assertThrows(IllegalStateException.class, () -> adoptionService.create(1L, "user", null));
        assertEquals("You already requested this pet.", ex.getMessage());
        verify(userService).getByUsername("user");
        verify(petService).get(1L);
        verify(adoptionRepository).existsByAdopterIdAndPetId(1L, 1L);
    }

    @Test
    public void whenRequestDoesntExist_create_createsIt() {
        var user = new User();
        user.setId(1L);
        when(userService.getByUsername("user")).thenReturn(user);
        when(petService.get(1L)).thenReturn(new Pet());
        when(adoptionRepository.existsByAdopterIdAndPetId(1L, 1L)).thenReturn(false);
        when(adoptionRepository.save(any(Adoption.class))).thenReturn(new Adoption());
        adoptionService.create(1L, "user", new AdoptionCreateRequest(null));
        verify(userService).getByUsername("user");
        verify(petService).get(1L);
        verify(adoptionRepository).existsByAdopterIdAndPetId(1L, 1L);
        verify(adoptionRepository).save(any(Adoption.class));
    }

    @Test
    public void whenRequestsExist_getRequestsForShelter_returnsThem() {
        when(adoptionRepository.findByPetShelterId(1L)).thenReturn(List.of());
        var all = adoptionService.getRequestsForShelter(1L);
        assertEquals(0, all.size());
        verify(adoptionRepository).findByPetShelterId(1L);
    }

    @Test
    public void whenRequestsExist_getRequestsForAdopter_returnsThem() {
        when(adoptionRepository.findByAdopterId(1L)).thenReturn(List.of());
        var all = adoptionService.getRequestsForAdopter(1L);
        assertEquals(0, all.size());
        verify(adoptionRepository).findByAdopterId(1L);
    }

    @Test
    public void whenRequestExists_get_returnsIt() {
        when(adoptionRepository.findById(1L)).thenReturn(Optional.of(new Adoption()));
        var result = adoptionService.get(1L);
        assertNotNull(result);
        verify(adoptionRepository).findById(1L);
    }

    @Test
    public void whenRequestExists_approve_approvesIt() {
        var request = new Adoption();
        var pet = new Pet();
        pet.setId(1L);
        request.setPet(pet);
        when(adoptionRepository.findById(1L)).thenReturn(Optional.of(request));
        doNothing().when(petService).markAdopted(1L);
        when(adoptionRepository.save(any(Adoption.class))).thenReturn(new Adoption());
        doNothing().when(appointmentService).cancelAllForPet(1L);
        when(adoptionRepository.findByPetIdAndStatus(1L, AdoptionStatus.Pending)).thenReturn(List.of());
        adoptionService.approve(1L);
        verify(adoptionRepository).findById(1L);
        verify(petService).markAdopted(1L);
        verify(adoptionRepository).save(any(Adoption.class));
        verify(appointmentService).cancelAllForPet(1L);
        verify(adoptionRepository).findByPetIdAndStatus(1L, AdoptionStatus.Pending);
    }

    @Test
    public void whenRequestExists_reject_rejectsIt() {
        when(adoptionRepository.findById(1L)).thenReturn(Optional.of(new Adoption()));
        when(adoptionRepository.save(any(Adoption.class))).thenReturn(new Adoption());
        adoptionService.reject(1L);
        verify(adoptionRepository).findById(1L);
        verify(adoptionRepository).save(any(Adoption.class));
    }

    @Test
    public void always_count_countsAdoptions() {
        when(adoptionRepository.count()).thenReturn(1L);
        var count = adoptionService.count();
        assertEquals(1L, count);
        verify(adoptionRepository).count();
    }

    @Test
    public void always_countByStatus_countsByStatus() {
        when(adoptionRepository.countByStatus(any(AdoptionStatus.class))).thenReturn(1L);
        var count = adoptionService.countByStatus(AdoptionStatus.Pending);
        assertEquals(1L, count);
        verify(adoptionRepository).countByStatus(any(AdoptionStatus.class));
    }
}