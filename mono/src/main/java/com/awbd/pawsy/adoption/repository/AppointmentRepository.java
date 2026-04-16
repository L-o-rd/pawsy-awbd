package com.awbd.pawsy.adoption.repository;

import com.awbd.pawsy.adoption.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByPetIdAndAppointmentDate(Long petId, LocalDate appointmentDate);
    List<Appointment> findByPetId(Long petId);
}
