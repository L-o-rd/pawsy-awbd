package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.dto.PetCreateRequest;
import com.awbd.pawsy.pet.dto.PetUpdateRequest;
import com.awbd.pawsy.pet.model.PetSex;
import com.awbd.pawsy.pet.model.PetStatus;
import com.awbd.pawsy.pet.specification.PetSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import com.awbd.pawsy.pet.repository.PetRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import com.awbd.pawsy.pet.dto.PetSummary;
import com.awbd.pawsy.pet.dto.PetMapper;
import static java.util.Objects.isNull;
import lombok.RequiredArgsConstructor;
import com.awbd.pawsy.pet.model.Pet;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {
    private final ShelterService shelterService;
    private final PetRepository petRepository;
    private final PetMapper petMapper;

    public Page<PetSummary> search(String name, String species, String sex, Long shelterId, String sort, Integer page, Integer size) {
        Specification<Pet> spec = (root, query, cb) -> cb.conjunction();

        if (!isNull(name) && !name.isBlank()) {
            spec = spec.and(PetSpecifications.nameContains(name));
        }

        if (!isNull(species) && !species.isBlank()) {
            spec = spec.and(PetSpecifications.hasSpecies(species));
        }

        if (!isNull(sex) && !sex.isBlank()) {
            spec = spec.and(PetSpecifications.hasSex(sex));
        }

        if (!isNull(shelterId)) {
            spec = spec.and(PetSpecifications.hasShelter(shelterId));
        }

        var sorting = switch (sort) {
            case "age" -> Sort.by("age").ascending();
            case "species" -> Sort.by("species").ascending();
            default -> Sort.by("name").ascending();
        };

        Pageable finalPageable = PageRequest.of(page, size, sorting);
        return petRepository.findAll(spec, finalPageable).map(petMapper::toSummary);
    }

    public Page<PetSummary> getPetsByManager(String username, Integer page, Integer size) {
        var shelter = shelterService.getByManager(username).orElseThrow(() -> new EntityNotFoundException("User `%s` has no shelter.".formatted(username)));
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());
        return petRepository.findByShelterId(shelter.id(), pageable).map(petMapper::toSummary);
    }

    public PetSummary create(PetCreateRequest dto, Long shelterId) {
        var shelter = shelterService.get(shelterId);
        var pet = new Pet();
        pet.setAge(dto.age());
        pet.setSex(PetSex.valueOf(dto.sex()));
        pet.setName(dto.name());
        pet.setPhoto(dto.photo());
        pet.setDescription(dto.description());
        pet.setShelter(shelter);
        pet.setSpecies(dto.species());
        pet.setStatus(PetStatus.Available);
        log.info("Pet `{}` added to shelter `{}`.", dto.name(), shelter.getId());
        return petMapper.toSummary(petRepository.save(pet));
    }

    public Pet get(Long id) {
        return petRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pet with id %d was not found.".formatted(id)));
    }

    public Optional<PetSummary> getById(Long id) {
        return petRepository.findById(id).map(petMapper::toSummary);
    }

    public PetUpdateRequest getForUpdate(Long id) {
        return petMapper.toUpdateRequest(get(id));
    }

    public void update(Long id, PetUpdateRequest dto) {
        var pet = get(id);
        pet.setName(dto.name());
        if (!isNull(dto.photo()))
            pet.setPhoto(dto.photo());

        pet.setSpecies(dto.species());
        pet.setSex(PetSex.valueOf(dto.sex()));
        pet.setAge(dto.age());
        pet.setDescription(dto.description());
        petRepository.save(pet);
    }

    public void delete(Long id) {
        petRepository.delete(get(id));
    }
}
