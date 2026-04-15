package com.awbd.pawsy.adoption.repository;

import com.awbd.pawsy.adoption.model.AdoptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.awbd.pawsy.adoption.model.Adoption;
import java.util.List;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
    List<Adoption> findByPetIdAndStatus(Long petId, AdoptionStatus status);
    boolean existsByAdopterIdAndPetId(Long adopterId, Long petId);
    List<Adoption> findByPetShelterId(Long shelterId);
    List<Adoption> findByAdopterId(Long adopterId);
}
