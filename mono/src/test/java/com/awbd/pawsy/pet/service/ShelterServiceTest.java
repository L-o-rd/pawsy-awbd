package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.dto.ShelterCreateRequest;
import com.awbd.pawsy.pet.dto.ShelterMapper;
import com.awbd.pawsy.pet.dto.ShelterSummary;
import com.awbd.pawsy.pet.model.Shelter;
import com.awbd.pawsy.pet.repository.ShelterRepository;
import com.awbd.pawsy.user.model.User;
import com.awbd.pawsy.user.service.UserService;
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
class ShelterServiceTest {

    @Mock
    private ShelterRepository shelterRepository;

    @Mock
    private ShelterMapper shelterMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ShelterService shelterService;

    @Test
    public void always_all_returnsAllShelters() {
        when(shelterRepository.findAll()).thenReturn(List.of(new Shelter()));
        var all = shelterService.all();
        assertEquals(1, all.size());
        verify(shelterRepository).findAll();
    }

    @Test
    public void whenShelterExists_get_getsTheShelter() {
        when(shelterRepository.findById(any(Long.class))).thenReturn(Optional.of(new Shelter()));
        var shelter = shelterService.get(1L);
        assertNotNull(shelter);
        verify(shelterRepository).findById(any(Long.class));
    }

    @Test
    public void whenManagerExists_getByManager_getsTheShelter() {
        when(shelterRepository.findByManagerId(any(Long.class))).thenReturn(Optional.of(new Shelter()));
        final var user = new User();
        user.setId(1L);
        var shelter = shelterService.getByManager(user);
        assertNotNull(shelter);
        verify(shelterRepository).findByManagerId(any(Long.class));
    }

    @Test
    public void withNoFilters_search_returnsAllShelters() {
        final var page = new PageImpl<Shelter>(List.of(), PageRequest.of(0, 6), 0);
        when(shelterRepository.findAll(ArgumentMatchers.<Specification<Shelter>>any(), any(Pageable.class))).thenReturn(page);
        var all = shelterService.search(null, null, "", 0, 6);
        assertEquals(0, all.getContent().size());
        verify(shelterRepository).findAll(ArgumentMatchers.<Specification<Shelter>>any(), any(Pageable.class));
    }

    @Test
    public void withFilters_search_returnsFilteredShelters() {
        final var page = new PageImpl<>(List.of(new Shelter()), PageRequest.of(0, 6), 1);
        when(shelterRepository.findAll(ArgumentMatchers.<Specification<Shelter>>any(), any(Pageable.class))).thenReturn(page);
        var all = shelterService.search("name", "location", "location", 0, 6);
        assertEquals(1, all.getContent().size());
        verify(shelterRepository).findAll(ArgumentMatchers.<Specification<Shelter>>any(), any(Pageable.class));
    }

    @Test
    public void withBlankFilters_search_returnsAllShelters() {
        final var page = new PageImpl<>(List.of(new Shelter()), PageRequest.of(0, 6), 1);
        when(shelterRepository.findAll(ArgumentMatchers.<Specification<Shelter>>any(), any(Pageable.class))).thenReturn(page);
        var all = shelterService.search("", "", "", 0, 6);
        assertEquals(1, all.getContent().size());
        verify(shelterRepository).findAll(ArgumentMatchers.<Specification<Shelter>>any(), any(Pageable.class));
    }

    @Test
    public void always_create_createsAShelter() {
        final var dto = new ShelterCreateRequest("shelter", null, null, null);
        final var user = new User();
        when(shelterRepository.save(any(Shelter.class))).thenReturn(new Shelter());
        doNothing().when(userService).makeManager(user);
        shelterService.create(dto, user);
        verify(shelterRepository).save(any(Shelter.class));
        verify(userService).makeManager(user);
    }

    @Test
    public void always_summary_createsASummary() {
        when(shelterRepository.findById(any(Long.class))).thenReturn(Optional.of(new Shelter()));
        when(shelterMapper.toSummary(any(Shelter.class))).thenReturn(new ShelterSummary(1L, "shelter", null, null, null, null));
        var dto = shelterService.summary(1L);
        assertEquals("shelter", dto.name());
        verify(shelterRepository).findById(any(Long.class));
        verify(shelterMapper).toSummary(any(Shelter.class));
    }

    @Test
    public void always_count_countsShelters() {
        when(shelterRepository.count()).thenReturn(1L);
        var count = shelterService.count();
        assertEquals(1L, count);
        verify(shelterRepository).count();
    }
}