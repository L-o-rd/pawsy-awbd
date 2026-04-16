package com.awbd.pawsy.adoption.dto;

import com.awbd.pawsy.adoption.model.Appointment;
import com.awbd.pawsy.pet.dto.PetMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = PetMapper.class)
public interface AppointmentMapper {
    AppointmentSummary toSummary(Appointment appointment);
}
