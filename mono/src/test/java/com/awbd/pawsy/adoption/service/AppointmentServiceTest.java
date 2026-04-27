package com.awbd.pawsy.adoption.service;

import com.awbd.pawsy.adoption.dto.AppointmentCreateRequest;
import com.awbd.pawsy.adoption.dto.AppointmentMapper;
import com.awbd.pawsy.adoption.model.Appointment;
import com.awbd.pawsy.adoption.model.AppointmentStatus;
import com.awbd.pawsy.adoption.repository.AppointmentRepository;
import com.awbd.pawsy.pet.model.Pet;
import com.awbd.pawsy.pet.model.PetStatus;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.user.model.User;
import com.awbd.pawsy.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private UserService userService;

    @Mock
    private PetService petService;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    public void whenAppointmentsExist_getForAdopter_returnsThem() {
        when(appointmentRepository.findByAdopterId(1L)).thenReturn(List.of());
        var all = appointmentService.getForAdopter(1L);
        assertEquals(0, all.size());
        verify(appointmentRepository).findByAdopterId(1L);
    }

    @Test
    public void whenAppointmentsExist_getBookedDates_returnTheirDates() {
        when(appointmentRepository.findByPetId(1L)).thenReturn(List.of());
        var all = appointmentService.getBookedDates(1L);
        assertEquals(0, all.size());
        verify(appointmentRepository).findByPetId(1L);
    }

    @Test
    public void whenOngoingAppointmentsExist_getBookedDates_returnTheirDates() {
        var app = new Appointment();
        app.setStatus(AppointmentStatus.Ongoing);
        when(appointmentRepository.findByPetId(1L)).thenReturn(List.of(app));
        var all = appointmentService.getBookedDates(1L);
        assertEquals(1, all.size());
        verify(appointmentRepository).findByPetId(1L);
    }

    @Test
    public void whenDoneAppointmentsExist_getBookedDates_returnTheirDates() {
        var app = new Appointment();
        app.setStatus(AppointmentStatus.Done);
        when(appointmentRepository.findByPetId(1L)).thenReturn(List.of(app));
        var all = appointmentService.getBookedDates(1L);
        assertEquals(1, all.size());
        verify(appointmentRepository).findByPetId(1L);
    }

    @Test
    public void whenPetIsAlreadyAdopted_create_throwsIllegalStateException() {
        var username = "user";
        var user = new User();
        var pet = new Pet();
        pet.setStatus(PetStatus.Adopted);
        when(userService.getByUsername(username)).thenReturn(user);
        when(petService.get(1L)).thenReturn(pet);
        var ex = assertThrows(IllegalStateException.class, () -> appointmentService.create(username, 1L, new AppointmentCreateRequest(LocalDate.MAX)));
        assertEquals("This pet has already been adopted!", ex.getMessage());
        verify(userService).getByUsername(username);
        verify(petService).get(1L);
    }

    @Test
    public void whenAppointmentExists_create_throwsIllegalStateException() {
        var username = "user";
        var user = new User();
        var pet = new Pet();
        user.setId(1L);
        when(userService.getByUsername(username)).thenReturn(user);
        when(petService.get(1L)).thenReturn(pet);
        when(appointmentRepository.existsByPetIdAndAdopterIdAndStatus(1L, user.getId(), AppointmentStatus.Ongoing)).thenReturn(true);
        var ex = assertThrows(IllegalStateException.class, () -> appointmentService.create(username, 1L, new AppointmentCreateRequest(null)));
        assertEquals("You already have an active appointment for this pet!", ex.getMessage());
        verify(userService).getByUsername(username);
        verify(petService).get(1L);
        verify(appointmentRepository).existsByPetIdAndAdopterIdAndStatus(1L, user.getId(), AppointmentStatus.Ongoing);
    }

    @Test
    public void whenAppointmentExistsOnThatDay_create_throwsIllegalStateException() {
        var dto = new AppointmentCreateRequest(LocalDate.MAX);
        var username = "user";
        var user = new User();
        var pet = new Pet();
        user.setId(1L);
        when(userService.getByUsername(username)).thenReturn(user);
        when(petService.get(1L)).thenReturn(pet);
        when(appointmentRepository.existsByPetIdAndAdopterIdAndStatus(1L, user.getId(), AppointmentStatus.Ongoing)).thenReturn(false);
        var app = new Appointment();
        app.setStatus(AppointmentStatus.Ongoing);
        when(appointmentRepository.findByPetIdAndAppointmentDate(1L, dto.appointmentDate())).thenReturn(Optional.of(app));
        var ex = assertThrows(IllegalStateException.class, () -> appointmentService.create(username, 1L, dto));
        assertEquals("Date already booked!", ex.getMessage());
        verify(userService).getByUsername(username);
        verify(petService).get(1L);
        verify(appointmentRepository).existsByPetIdAndAdopterIdAndStatus(1L, user.getId(), AppointmentStatus.Ongoing);
        verify(appointmentRepository).findByPetIdAndAppointmentDate(eq(1L), any(LocalDate.class));
    }

    @Test
    public void whenAppointmentExistsOnThatDayButDone_create_throwsIllegalStateException() {
        var dto = new AppointmentCreateRequest(LocalDate.MAX);
        var username = "user";
        var user = new User();
        var pet = new Pet();
        user.setId(1L);
        when(userService.getByUsername(username)).thenReturn(user);
        when(petService.get(1L)).thenReturn(pet);
        when(appointmentRepository.existsByPetIdAndAdopterIdAndStatus(1L, user.getId(), AppointmentStatus.Ongoing)).thenReturn(false);
        var app = new Appointment();
        app.setStatus(AppointmentStatus.Done);
        when(appointmentRepository.findByPetIdAndAppointmentDate(1L, dto.appointmentDate())).thenReturn(Optional.of(app));
        var ex = assertThrows(IllegalStateException.class, () -> appointmentService.create(username, 1L, dto));
        assertEquals("Date already booked!", ex.getMessage());
        verify(userService).getByUsername(username);
        verify(petService).get(1L);
        verify(appointmentRepository).existsByPetIdAndAdopterIdAndStatus(1L, user.getId(), AppointmentStatus.Ongoing);
        verify(appointmentRepository).findByPetIdAndAppointmentDate(eq(1L), any(LocalDate.class));
    }

    @Test
    public void whenAppointmentWasCancelled_create_makesANewOne() {
        var dto = new AppointmentCreateRequest(LocalDate.MAX);
        var username = "user";
        var user = new User();
        var pet = new Pet();
        user.setId(1L);
        when(userService.getByUsername(username)).thenReturn(user);
        when(petService.get(1L)).thenReturn(pet);
        when(appointmentRepository.existsByPetIdAndAdopterIdAndStatus(1L, user.getId(), AppointmentStatus.Ongoing)).thenReturn(false);
        var app = new Appointment();
        app.setStatus(AppointmentStatus.Cancelled);
        when(appointmentRepository.findByPetIdAndAppointmentDate(1L, dto.appointmentDate())).thenReturn(Optional.of(app));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(new Appointment());
        appointmentService.create(username, 1L, dto);
        verify(userService).getByUsername(username);
        verify(petService).get(1L);
        verify(appointmentRepository).existsByPetIdAndAdopterIdAndStatus(1L, user.getId(), AppointmentStatus.Ongoing);
        verify(appointmentRepository).findByPetIdAndAppointmentDate(eq(1L), any(LocalDate.class));
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    public void whenAppointmentIsValid_create_savesIt() {
        var dto = new AppointmentCreateRequest(LocalDate.MAX);
        var username = "user";
        var user = new User();
        var pet = new Pet();
        user.setId(1L);
        when(userService.getByUsername(username)).thenReturn(user);
        when(petService.get(1L)).thenReturn(pet);
        when(appointmentRepository.existsByPetIdAndAdopterIdAndStatus(1L, user.getId(), AppointmentStatus.Ongoing)).thenReturn(false);
        when(appointmentRepository.findByPetIdAndAppointmentDate(1L, dto.appointmentDate())).thenReturn(Optional.empty());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(new Appointment());
        appointmentService.create(username, 1L, dto);
        verify(userService).getByUsername(username);
        verify(petService).get(1L);
        verify(appointmentRepository).existsByPetIdAndAdopterIdAndStatus(1L, user.getId(), AppointmentStatus.Ongoing);
        verify(appointmentRepository).findByPetIdAndAppointmentDate(eq(1L), any(LocalDate.class));
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    public void whenAppointmentExists_get_returnsIt() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(new Appointment()));
        var app = appointmentService.get(1L);
        assertNotNull(app);
        verify(appointmentRepository).findById(1L);
    }

    @Test
    public void whenAppointmentExistsAndIsOngoing_cancel_cancelsIt() {
        var app = new Appointment();
        app.setStatus(AppointmentStatus.Ongoing);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(app));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(new Appointment());
        appointmentService.cancel(1L);
        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    public void whenAppointmentExistsButIsNotOngoing_cancel_throwsIllegalStateException() {
        var app = new Appointment();
        app.setStatus(AppointmentStatus.Done);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(app));
        var ex = assertThrows(IllegalStateException.class, () -> appointmentService.cancel(1L));
        assertEquals("Appointment cannot be cancelled.", ex.getMessage());
        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository, times(0)).save(any(Appointment.class));
    }

    @Test
    public void always_cancelAllForPet_cancelsAllOngoing() {
        var app = new Appointment();
        app.setStatus(AppointmentStatus.Ongoing);
        var apps = List.of(app);
        when(appointmentRepository.findByPetIdAndStatus(1L, AppointmentStatus.Ongoing)).thenReturn(apps);
        when(appointmentRepository.saveAll(any())).thenReturn(List.of());
        appointmentService.cancelAllForPet(1L);
        assertEquals(AppointmentStatus.Cancelled, apps.getFirst().getStatus());
        verify(appointmentRepository).findByPetIdAndStatus(1L, AppointmentStatus.Ongoing);
        verify(appointmentRepository).saveAll(any());
    }

    @Test
    public void always_count_countsAppointments() {
        when(appointmentRepository.count()).thenReturn(1L);
        var count = appointmentService.count();
        assertEquals(1L, count);
        verify(appointmentRepository).count();
    }

    @Test
    public void always_countByStatus_countsAppointmentsByStatus() {
        when(appointmentRepository.countByStatus(any(AppointmentStatus.class))).thenReturn(1L);
        var count = appointmentService.countByStatus(AppointmentStatus.Ongoing);
        assertEquals(1L, count);
        verify(appointmentRepository).countByStatus(any(AppointmentStatus.class));
    }
}