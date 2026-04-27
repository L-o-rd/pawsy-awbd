package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.dto.ShelterCreateRequest;
import com.awbd.pawsy.pet.dto.ShelterMapper;
import com.awbd.pawsy.pet.dto.ShelterSummary;
import com.awbd.pawsy.pet.repository.ShelterRepository;
import com.awbd.pawsy.pet.specification.ShelterSpecifications;
import com.awbd.pawsy.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.awbd.pawsy.pet.model.Shelter;
import lombok.RequiredArgsConstructor;
import com.awbd.pawsy.user.model.User;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShelterService {
    private final ShelterRepository shelterRepository;
    private final ShelterMapper shelterMapper;
    private final UserService userService;

    public List<Shelter> all() {
        return shelterRepository.findAll();
    }

    public Shelter get(Long id) {
        return shelterRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Shelter with id %d was not found.".formatted(id)));
    }

    public Shelter getByManager(User user) {
        return shelterRepository.findByManagerId(user.getId()).orElseThrow(() -> new EntityNotFoundException("Manager with id %d has no shelter.".formatted(user.getId())));
    }

    public Page<ShelterSummary> search(String name, String location, String sort, Integer page, Integer size) {
        Specification<Shelter> spec = (root, query, cb) -> cb.conjunction();

        if (!isNull(name) && !name.isBlank()) {
            spec = spec.and(ShelterSpecifications.nameContains(name));
        }

        if (!isNull(location) && !location.isBlank()) {
            spec = spec.and(ShelterSpecifications.locationContains(location));
        }

        var sorting = switch (sort) {
            case "location" -> Sort.by("location").ascending();
            default -> Sort.by("name").ascending();
        };

        Pageable finalPageable = PageRequest.of(
            page,
            size,
            sorting
        );

        return shelterRepository.findAll(spec, finalPageable).map(shelterMapper::toSummary);
    }

    @Transactional
    public void create(ShelterCreateRequest dto, User user) {
        var shelter = new Shelter();
        shelter.setName(dto.name());
        shelter.setLocation(dto.location());
        shelter.setEmail(dto.email());
        shelter.setPhone(dto.phone());
        shelter.setManager(user);
        shelterRepository.save(shelter);
        userService.makeManager(user);
        log.info("Shelter `{}` created, managed by user `{}`.", dto.name(), user.getUsername());
    }

    public ShelterSummary summary(Long id) {
        return shelterMapper.toSummary(get(id));
    }

    public Long count() {
        return shelterRepository.count();
    }
}
