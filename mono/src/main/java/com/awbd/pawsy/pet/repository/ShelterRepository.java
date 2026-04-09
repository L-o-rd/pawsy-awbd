package com.awbd.pawsy.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.awbd.pawsy.pet.model.Shelter;

public interface ShelterRepository extends JpaRepository<Shelter, Long> {}
