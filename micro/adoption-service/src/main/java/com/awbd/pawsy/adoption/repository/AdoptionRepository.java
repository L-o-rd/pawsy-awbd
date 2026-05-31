package com.awbd.pawsy.adoption.repository;

import com.awbd.pawsy.adoption.model.AdoptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.awbd.pawsy.adoption.model.Adoption;
import java.util.List;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
    List<Adoption> findByPetIdAndStatus(Long petId, AdoptionStatus status);
    boolean existsByAdopterAndPetId(String adopter, Long petId);
    List<Adoption> findByShelterId(Long shelterId);
    List<Adoption> findByAdopter(String adopter);
    Long countByStatus(AdoptionStatus status);
    boolean existsByPetId(Long petId);
}
