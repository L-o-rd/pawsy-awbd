package com.awbd.pawsy.adoption.dto;

import com.awbd.pawsy.adoption.model.Appointment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    AppointmentSummary toSummary(Appointment appointment);
}
