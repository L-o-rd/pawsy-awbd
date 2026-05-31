package com.awbd.pawsy.adoption.dto;

import com.awbd.pawsy.adoption.model.Adoption;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdoptionMapper {
    @Mapping(target = "adopterName", source = "adopter")
    AdoptionSummary toSummary(Adoption adoption);
}
