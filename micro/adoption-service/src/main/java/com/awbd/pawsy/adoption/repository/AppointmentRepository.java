package com.awbd.pawsy.adoption.repository;

import com.awbd.pawsy.adoption.model.Appointment;
import com.awbd.pawsy.adoption.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByPetIdAndAdopterAndStatus(Long petId, String adopter, AppointmentStatus status);
    Optional<Appointment> findByPetIdAndAppointmentDate(Long petId, LocalDate appointmentDate);
    boolean existsByPetIdAndAppointmentDate(Long petId, LocalDate appointmentDate);
    List<Appointment> findByPetIdAndStatus(Long petId, AppointmentStatus status);
    List<Appointment> findByAdopter(String adopter);
    Long countByStatus(AppointmentStatus status);
    List<Appointment> findByPetId(Long petId);
    boolean existsByPetId(Long petId);
}
