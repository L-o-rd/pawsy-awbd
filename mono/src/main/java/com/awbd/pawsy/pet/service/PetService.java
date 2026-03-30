package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.model.Pet;
import com.awbd.pawsy.pet.repository.PetRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;

    public List<Pet> all(Map<String, String> filters) {
        var sortBy = filters.getOrDefault("sort-by", null);
        if (isNull(sortBy) || sortBy.isBlank()) {
            return petRepository.findAll();
        }

        return petRepository.findAll(Sort.by(sortBy));
    }
}
