package com.awbd.pawsy.adoption.service;

import com.awbd.pawsy.adoption.dto.AdoptionCreateRequest;
import com.awbd.pawsy.adoption.dto.AdoptionMapper;
import com.awbd.pawsy.adoption.dto.AdoptionSummary;
import com.awbd.pawsy.adoption.model.Adoption;
import com.awbd.pawsy.adoption.model.AdoptionStatus;
import com.awbd.pawsy.adoption.repository.AdoptionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdoptionService {
    private final AdoptionRepository adoptionRepository;
    private final AppointmentService appointmentService;
    private final AdoptionMapper adoptionMapper;

    public Boolean anyForPet(Long petId) {
        return adoptionRepository.existsByPetId(petId);
    }

    public void create(Long petId, Long shelterId, String username, AdoptionCreateRequest dto) {
        if (adoptionRepository.existsByAdopterAndPetId(username, petId)) {
            log.error("Adopter `{}` tried to request the same pet `{}`.", username, petId);
            throw new IllegalStateException("You already requested this pet.");
        }

        var adoption = new Adoption();
        adoption.setAdopter(username);
        adoption.setPetId(petId);
        adoption.setShelterId(shelterId);
        adoption.setRequestDate(LocalDateTime.now());
        adoption.setStatus(AdoptionStatus.Pending);
        adoption.setApprovalDate(null);
        adoptionRepository.save(adoption);
        log.info("Adoption request by `{}` for pet `{}` created successfully.", username, petId);
    }

    public List<AdoptionSummary> getRequestsForShelter(Long shelterId) {
        return adoptionRepository.findByShelterId(shelterId)
                .stream()
                .map(adoptionMapper::toSummary)
                .toList();
    }

    public List<AdoptionSummary> getRequestsForAdopter(String adopter) {
        return adoptionRepository.findByAdopter(adopter)
                .stream()
                .map(adoptionMapper::toSummary)
                .toList();
    }

    public Adoption get(Long id) {
        return adoptionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Adoption with id %d was not found.".formatted(id)));
    }

    public AdoptionSummary getById(Long id) {
        return adoptionMapper.toSummary(get(id));
    }

    @Transactional
    public void approve(Long id) {
        var adoption = get(id);
        adoption.setStatus(AdoptionStatus.Approved);
        adoption.setApprovalDate(LocalDateTime.now());
        adoptionRepository.save(adoption);
        appointmentService.cancelAllForPet(adoption.getPetId());

        var others = adoptionRepository.findByPetIdAndStatus(adoption.getPetId(), AdoptionStatus.Pending);
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
