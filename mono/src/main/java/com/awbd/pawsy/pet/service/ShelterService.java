package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.repository.ShelterRepository;
import org.springframework.stereotype.Service;
import com.awbd.pawsy.pet.model.Shelter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelterService {
    private final ShelterRepository shelterRepository;

    public List<Shelter> all() {
        return shelterRepository.findAll();
    }
}
