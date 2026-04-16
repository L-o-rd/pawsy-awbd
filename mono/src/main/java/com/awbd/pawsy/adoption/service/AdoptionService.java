package com.awbd.pawsy.adoption.service;

import com.awbd.pawsy.adoption.dto.AdoptionCreateRequest;
import com.awbd.pawsy.adoption.dto.AdoptionMapper;
import com.awbd.pawsy.adoption.dto.AdoptionSummary;
import com.awbd.pawsy.adoption.model.Adoption;
import com.awbd.pawsy.adoption.model.AdoptionStatus;
import com.awbd.pawsy.adoption.repository.AdoptionRepository;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionService {
    private final AdoptionRepository adoptionRepository;
    private final AppointmentService appointmentService;
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

    public List<AdoptionSummary> getRequestsForShelter(Long shelterId) {
        return adoptionRepository.findByPetShelterId(shelterId)
                .stream()
                .map(adoptionMapper::toSummary)
                .toList();
    }

    public List<AdoptionSummary> getRequestsForAdopter(Long adopterId) {
        return adoptionRepository.findByAdopterId(adopterId)
                .stream()
                .map(adoptionMapper::toSummary)
                .toList();
    }

    public Adoption get(Long id) {
        return adoptionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Adoption with id %d was not found.".formatted(id)));
    }

    @Transactional
    public void approve(Long id) {
        var adoption = get(id);
        adoption.setStatus(AdoptionStatus.Approved);
        adoption.setApprovalDate(LocalDateTime.now());
        petService.markAdopted(adoption.getPet().getId());
        adoptionRepository.save(adoption);
        appointmentService.cancelAllForPet(adoption.getPet().getId());

        var others = adoptionRepository.findByPetIdAndStatus(adoption.getPet().getId(), AdoptionStatus.Pending);
        others.stream().map(Adoption::getId).forEach(this::reject);
    }

    @Transactional
    public void reject(Long id) {
        var adoption = get(id);
        adoption.setStatus(AdoptionStatus.Rejected);
        adoption.setApprovalDate(LocalDateTime.now());
        adoptionRepository.save(adoption);
    }

    public Long count() {
        return adoptionRepository.count();
    }

    public Long countByStatus(AdoptionStatus status) {
        return adoptionRepository.countByStatus(status);
    }
}
