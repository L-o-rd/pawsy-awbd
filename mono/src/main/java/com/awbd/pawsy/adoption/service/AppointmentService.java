package com.awbd.pawsy.adoption.service;

import com.awbd.pawsy.adoption.dto.AppointmentCreateRequest;
import com.awbd.pawsy.adoption.dto.AppointmentMapper;
import com.awbd.pawsy.adoption.dto.AppointmentSummary;
import com.awbd.pawsy.adoption.model.Appointment;
import com.awbd.pawsy.adoption.model.AppointmentStatus;
import com.awbd.pawsy.adoption.repository.AppointmentRepository;
import com.awbd.pawsy.pet.model.PetStatus;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserService userService;
    private final PetService petService;

    public List<AppointmentSummary> getForAdopter(Long adopterId) {
        return appointmentRepository.findByAdopterId(adopterId)
                .stream()
                .map(appointmentMapper::toSummary)
                .toList();
    }

    public List<LocalDate> getBookedDates(Long petId) {
        return appointmentRepository.findByPetId(petId)
                .stream()
                .map(Appointment::getAppointmentDate)
                .toList();
    }

    public void create(String username, Long petId, AppointmentCreateRequest dto) {
        var user = userService.getByUsername(username);
        var pet = petService.get(petId);

        if (pet.getStatus() == PetStatus.Adopted) {
            throw new IllegalStateException("This pet has already been adopted!");
        }

        if (appointmentRepository.existsByPetIdAndAdopterIdAndStatus(
                petId, user.getId(), AppointmentStatus.Ongoing)) {
            throw new IllegalStateException("You already have an active appointment for this pet!");
        }

        if (appointmentRepository.existsByPetIdAndAppointmentDate(petId, dto.appointmentDate())) {
            throw new IllegalStateException("Date already booked!");
        }

        var appointment = new Appointment();
        appointment.setPet(pet);
        appointment.setAdopter(user);
        appointment.setAppointmentDate(dto.appointmentDate());
        appointment.setScheduledAtDate(LocalDateTime.now());
        appointment.setStatus(AppointmentStatus.Ongoing);
        appointmentRepository.save(appointment);
    }
}
