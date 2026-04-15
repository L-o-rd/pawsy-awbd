package com.awbd.pawsy.adoption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.awbd.pawsy.adoption.model.Adoption;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
    boolean existsByAdopterIdAndPetId(Long adopterId, Long petId);
}
