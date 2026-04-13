package com.awbd.pawsy.pet.dto;

import com.awbd.pawsy.pet.model.Shelter;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShelterMapper {
    @Mapping(target = "manager", source = "shelter.manager.username")
    ShelterSummary toSummary(Shelter shelter);
}
