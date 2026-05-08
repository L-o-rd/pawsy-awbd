package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.dto.ShelterMapper;
import com.awbd.pawsy.pet.repository.ShelterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.awbd.pawsy.pet.model.Shelter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShelterService {
    private final ShelterRepository shelterRepository;
    private final ShelterMapper shelterMapper;

    public List<Shelter> all() {
        return shelterRepository.findAll();
    }

    public Shelter get(Long id) {
        return shelterRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Shelter with id %d was not found.".formatted(id)));
    }
}
