package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.dto.PetCreateRequest;
import com.awbd.pawsy.pet.dto.PetMapper;
import com.awbd.pawsy.pet.dto.PetSummary;
import com.awbd.pawsy.pet.dto.PetUpdateRequest;
import com.awbd.pawsy.pet.model.Pet;
import com.awbd.pawsy.pet.model.PetStatus;
import com.awbd.pawsy.pet.model.Shelter;
import com.awbd.pawsy.pet.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private PetService petService;

    @Test
    public void whenPetsExist_getPetsForShelter_returnsThem() {
        final var page = new PageImpl<>(List.of(new Pet()), PageRequest.of(0, 6), 1);
        when(petRepository.findByShelterId(eq(1L), any(Pageable.class))).thenReturn(page);
        when(petMapper.toSummary(any(Pet.class))).thenReturn(new PetSummary(null, null, null, null, null, null, null, null, null));
        var shelter = new Shelter();
        shelter.setId(1L);
        var all = petService.getPetsForShelter(shelter, 0, 6);
        assertEquals(1, all.getContent().size());
        verify(petRepository).findByShelterId(eq(1L), any(Pageable.class));
    }

    @Test
    public void withNoFilters_search_returnsAllPets() {
        final var page = new PageImpl<Pet>(List.of(), PageRequest.of(0, 6), 0);
        when(petRepository.findAll(ArgumentMatchers.<Specification<Pet>>any(), any(Pageable.class))).thenReturn(page);
        var all = petService.search(null, null, null, null, "name", PageRequest.of(0, 6));
        assertEquals(0, all.getContent().size());
        verify(petRepository).findAll(ArgumentMatchers.<Specification<Pet>>any(), any(Pageable.class));
    }

    @Test
    public void withBlankFilters_search_returnsAllPets() {
        final var page = new PageImpl<Pet>(List.of(), PageRequest.of(0, 6), 0);
        when(petRepository.findAll(ArgumentMatchers.<Specification<Pet>>any(), any(Pageable.class))).thenReturn(page);
        var all = petService.search("", "", "", 1L, "age", PageRequest.of(0, 6));
        assertEquals(0, all.getContent().size());
        verify(petRepository).findAll(ArgumentMatchers.<Specification<Pet>>any(), any(Pageable.class));
    }

    @Test
    public void withFilters_search_returnsThosePets() {
        final var page = new PageImpl<Pet>(List.of(), PageRequest.of(0, 6), 0);
        when(petRepository.findAll(ArgumentMatchers.<Specification<Pet>>any(), any(Pageable.class))).thenReturn(page);
        var all = petService.search("name", "species", "sex", 1L, "species", PageRequest.of(0, 6));
        assertEquals(0, all.getContent().size());
        verify(petRepository).findAll(ArgumentMatchers.<Specification<Pet>>any(), any(Pageable.class));
    }

    @Test
    public void always_summary_makesASummary() {
        when(petMapper.toSummary(any(Pet.class))).thenReturn(new PetSummary(null, null, null, null, null, null, null, null, null));
        var result = petService.summary(new Pet());
        assertNotNull(result);
        verify(petMapper).toSummary(any(Pet.class));
    }

    @Test
    public void whenPetExists_get_getsIt() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(new Pet()));
        var pet = petService.get(1L);
        assertNotNull(pet);
        verify(petRepository).findById(1L);
    }

    @Test
    public void always_create_createsAPet() {
        final var dto = new PetCreateRequest(null, null, null, null, "Male", null);
        var shelter = new Shelter();
        when(petRepository.save(any(Pet.class))).thenReturn(new Pet());
        var pet = petService.create(dto, shelter);
        assertNotNull(pet);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    public void whenRelatedPetsExist_related_returnsThem() {
        var pets = petService.related(1L);
        assertEquals(0, pets.size());
    }

    @Test
    public void whenPetExists_getForUpdate_makesAnUpdateDto() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(new Pet()));
        when(petMapper.toUpdateRequest(any(Pet.class))).thenReturn(new PetUpdateRequest(null, null, null, null, null, null));
        var dto = petService.getForUpdate(1L);
        assertNotNull(dto);
        verify(petRepository).findById(1L);
        verify(petMapper).toUpdateRequest(any(Pet.class));
    }

    @Test
    public void whenPetExists_getShelterForPet_getsItsShelter() {
        var pet = new Pet();
        pet.setShelter(new Shelter());
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        var shelter = petService.getShelterForPet(1L);
        assertNotNull(shelter);
        verify(petRepository).findById(1L);
    }

    @Test
    public void whenPetExists_delete_deletesIt() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(new Pet()));
        doNothing().when(petRepository).delete(any(Pet.class));
        petService.delete(1L);
        verify(petRepository).findById(1L);
        verify(petRepository).delete(any(Pet.class));
    }

    @Test
    public void whenPetExists_update_updatesItWithNoPhoto() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(new Pet()));
        when(petRepository.save(any(Pet.class))).thenReturn(new Pet());
        petService.update(1L, new PetUpdateRequest(null, null, null, "Male", null, null));
        verify(petRepository).findById(1L);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    public void whenPetExists_update_updatesItWithPhoto() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(new Pet()));
        when(petRepository.save(any(Pet.class))).thenReturn(new Pet());
        petService.update(1L, new PetUpdateRequest(null, "photo", null, "Male", null, null));
        verify(petRepository).findById(1L);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    public void whenPetExists_markAdopted_marksItAsAdopted() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(new Pet()));
        var savedPet = new Pet();
        savedPet.setStatus(PetStatus.Adopted);
        when(petRepository.save(any(Pet.class))).thenReturn(savedPet);
        petService.markAdopted(1L);
        assertEquals(PetStatus.Adopted, savedPet.getStatus());
        verify(petRepository).findById(1L);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    public void always_count_countsPets() {
        when(petRepository.count()).thenReturn(1L);
        var count = petService.count();
        assertEquals(1L, count);
        verify(petRepository).count();
    }

    @Test
    public void always_countByStatus_returnsCountByStatus() {
        when(petRepository.countByStatus(any(PetStatus.class))).thenReturn(1L);
        var count = petService.countByStatus(PetStatus.Adopted);
        assertEquals(1L, count);
        verify(petRepository).countByStatus(any(PetStatus.class));
    }
}