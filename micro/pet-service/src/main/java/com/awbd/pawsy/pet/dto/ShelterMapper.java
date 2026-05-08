package com.awbd.pawsy.pet.dto;

import com.awbd.pawsy.pet.model.Shelter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShelterMapper {
    ShelterSummary toSummary(Shelter shelter);
}
