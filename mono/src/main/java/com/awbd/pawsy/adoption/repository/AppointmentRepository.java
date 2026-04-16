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
    boolean existsByPetIdAndAdopterIdAndStatus(Long petId, Long adopterId, AppointmentStatus status);
    Optional<Appointment> findByPetIdAndAppointmentDate(Long petId, LocalDate appointmentDate);
    boolean existsByPetIdAndAppointmentDate(Long petId, LocalDate appointmentDate);
    List<Appointment> findByPetIdAndStatus(Long petId, AppointmentStatus status);
    List<Appointment> findByAdopterId(Long adopterId);
    List<Appointment> findByPetId(Long petId);
}
