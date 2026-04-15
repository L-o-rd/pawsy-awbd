package com.awbd.pawsy.adoption.service;

import com.awbd.pawsy.adoption.dto.AdoptionCreateRequest;
import com.awbd.pawsy.adoption.dto.AdoptionMapper;
import com.awbd.pawsy.adoption.model.Adoption;
import com.awbd.pawsy.adoption.model.AdoptionStatus;
import com.awbd.pawsy.adoption.repository.AdoptionRepository;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.user.service.UserService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdoptionService {
    private final AdoptionRepository adoptionRepository;
    private final AdoptionMapper adoptionMapper;
    private final UserService userService;
    private final PetService petService;

    public void create(Long petId, String username, AdoptionCreateRequest dto) {
        var user = userService.getByUsername(username);
        var pet = petService.get(petId);
        if (adoptionRepository.existsByAdopterIdAndPetId(user.getId(), petId)) {
            throw new IllegalStateException("You already requested this pet.");
        }

        var adoption = new Adoption();
        adoption.setAdopter(user);
        adoption.setPet(pet);
        adoption.setRequestDate(LocalDateTime.now());
        adoption.setStatus(AdoptionStatus.Pending);
        adoption.setApprovalDate(null);
        adoptionRepository.save(adoption);
    }
}
