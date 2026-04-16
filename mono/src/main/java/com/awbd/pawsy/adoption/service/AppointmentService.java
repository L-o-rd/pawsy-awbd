package com.awbd.pawsy.adoption.service;

import com.awbd.pawsy.adoption.dto.AppointmentCreateRequest;
import com.awbd.pawsy.adoption.model.Appointment;
import com.awbd.pawsy.adoption.model.AppointmentStatus;
import com.awbd.pawsy.adoption.repository.AppointmentRepository;
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
    private final UserService userService;
    private final PetService petService;

    public List<LocalDate> getBookedDates(Long petId) {
        return appointmentRepository.findByPetId(petId)
                .stream()
                .map(Appointment::getAppointmentDate)
                .toList();
    }

    public void create(String username, Long petId, AppointmentCreateRequest dto) {
        var user = userService.getByUsername(username);
        var pet = petService.get(petId);

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
