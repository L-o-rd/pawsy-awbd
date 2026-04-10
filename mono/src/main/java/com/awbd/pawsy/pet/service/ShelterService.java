package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.repository.ShelterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import com.awbd.pawsy.pet.model.Shelter;
import lombok.RequiredArgsConstructor;
import com.awbd.pawsy.user.model.User;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelterService {
    private final ShelterRepository shelterRepository;

    public List<Shelter> all() {
        return shelterRepository.findAll();
    }

    public Shelter get(Long id) {
        return shelterRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Shelter with id %d was not found.".formatted(id)));
    }

    public Shelter getByManager(User user) {
        return shelterRepository.findByManagerId(user.getId()).orElseThrow(() -> new EntityNotFoundException("Manager with id %d has no shelter.".formatted(user.getId())));
    }
}
