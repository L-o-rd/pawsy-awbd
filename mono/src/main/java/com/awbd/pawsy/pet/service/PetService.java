package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.specification.PetSpecifications;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import com.awbd.pawsy.pet.repository.PetRepository;
import org.springframework.data.domain.Pageable;
import com.awbd.pawsy.pet.dto.PetCreateRequest;
import com.awbd.pawsy.pet.dto.PetUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import com.awbd.pawsy.pet.model.PetStatus;
import com.awbd.pawsy.pet.dto.PetSummary;
import com.awbd.pawsy.pet.dto.PetMapper;
import com.awbd.pawsy.pet.model.Shelter;
import static java.util.Objects.isNull;
import com.awbd.pawsy.pet.model.PetSex;
import lombok.RequiredArgsConstructor;
import com.awbd.pawsy.pet.model.Pet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final PetMapper petMapper;

    public Page<PetSummary> getPetsForShelter(Shelter shelter, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());
        return petRepository.findByShelterId(shelter.getId(), pageable)
                .map(petMapper::toSummary);
    }

    public Page<PetSummary> search(String name, String species, String sex, Long shelterId, String sort, Pageable pageable) {
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

        Pageable finalPageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            sorting
        );

        return petRepository.findAll(spec, finalPageable).map(petMapper::toSummary);
    }

    public PetSummary summary(Pet pet) {
        return petMapper.toSummary(pet);
    }

    public Pet get(Long id) {
        return petRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pet with id %d was not found.".formatted(id)));
    }

    public Pet create(PetCreateRequest dto, Shelter shelter) {
        var pet = new Pet();
        pet.setAge(dto.age());
        pet.setSex(PetSex.valueOf(dto.sex()));
        pet.setName(dto.name());
        pet.setPhoto(dto.photo());
        pet.setDescription(dto.description());
        pet.setShelter(shelter);
        pet.setSpecies(dto.species());
        pet.setStatus(PetStatus.Available);
        return petRepository.save(pet);
    }

    public List<PetSummary> related(Long ignored) {
        return List.of();
    }

    public PetUpdateRequest getForUpdate(Long id) {
        var pet = get(id);
        return petMapper.toUpdateRequest(pet);
    }

    public Shelter getShelterForPet(Long id) {
        var pet = get(id);
        return pet.getShelter();
    }

    public void delete(Long id) {
        var pet = get(id);
        petRepository.delete(pet);
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

    public void markAdopted(Long id) {
        var pet = get(id);
        pet.setStatus(PetStatus.Adopted);
        petRepository.save(pet);
    }

    public Long count() {
        return petRepository.count();
    }

    public Long countByStatus(PetStatus status) {
        return petRepository.countByStatus(status);
    }
}
