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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
                .filter(a -> a.getStatus().equals(AppointmentStatus.Ongoing) || a.getStatus().equals(AppointmentStatus.Done))
                .map(Appointment::getAppointmentDate)
                .toList();
    }

    public void create(String username, Long petId, AppointmentCreateRequest dto) {
        var user = userService.getByUsername(username);
        var pet = petService.get(petId);

        if (pet.getStatus() == PetStatus.Adopted) {
            log.error("Adopter `{}` tried to book an appointment for adopted pet `{}` on {}.", username, petId, dto.appointmentDate().toString());
            throw new IllegalStateException("This pet has already been adopted!");
        }

        if (appointmentRepository.existsByPetIdAndAdopterIdAndStatus(
                petId, user.getId(), AppointmentStatus.Ongoing)) {
            log.error("Adopter `{}` tried to book multiple appointments for pet `{}`.", username, petId);
            throw new IllegalStateException("You already have an active appointment for this pet!");
        }

        var thatDay = appointmentRepository.findByPetIdAndAppointmentDate(petId, dto.appointmentDate());
        if (thatDay.isPresent()) {
            var status = thatDay.get().getStatus();
            if (status.equals(AppointmentStatus.Ongoing) || status.equals(AppointmentStatus.Done)) {
                log.error("Appointment by adopter `{}` for pet `{}` on {} conflicts with other appointments.", username, petId, dto.appointmentDate().toString());
                throw new IllegalStateException("Date already booked!");
            }
        }

        var appointment = new Appointment();
        appointment.setPet(pet);
        appointment.setAdopter(user);
        appointment.setAppointmentDate(dto.appointmentDate());
        appointment.setScheduledAtDate(LocalDateTime.now());
        appointment.setStatus(AppointmentStatus.Ongoing);
        appointmentRepository.save(appointment);
        log.info("Appointment by `{}` for pet `{}` was booked on {}.", username, petId, dto.appointmentDate().toString());
    }

    public Appointment get(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Appointment %d was not found.".formatted(id)));
    }

    public void cancel(Long id) {
        var appointment = get(id);
        if (appointment.getStatus() != AppointmentStatus.Ongoing) {
            log.error("Tried to cancel a done or cancelled appointment ({}).", id);
            throw new IllegalStateException("Appointment cannot be cancelled.");
        }

        appointment.setStatus(AppointmentStatus.Cancelled);
        appointmentRepository.save(appointment);
        log.info("Appointment {} was cancelled.", id);
    }

    public void cancelAllForPet(Long petId) {
        var appointments = appointmentRepository.findByPetIdAndStatus(petId, AppointmentStatus.Ongoing);
        for (var a : appointments) a.setStatus(AppointmentStatus.Cancelled);
        appointmentRepository.saveAll(appointments);
    }

    public Long count() {
        return appointmentRepository.count();
    }

    public Long countByStatus(AppointmentStatus status) {
        return appointmentRepository.countByStatus(status);
    }
}
