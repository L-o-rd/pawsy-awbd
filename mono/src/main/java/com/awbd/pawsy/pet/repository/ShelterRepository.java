package com.awbd.pawsy.pet.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import com.awbd.pawsy.pet.model.Shelter;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long>, JpaSpecificationExecutor<Shelter> {
    Optional<Shelter> findByManagerId(Long managerId);
}
