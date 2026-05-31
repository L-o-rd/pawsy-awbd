package com.awbd.pawsy.adoption.dto;

import com.awbd.pawsy.adoption.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(target = "adopterName", source = "adopter")
    AppointmentSummary toSummary(Appointment appointment);
}
