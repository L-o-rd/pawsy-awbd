package com.awbd.pawsy.adoption.dto;

import com.awbd.pawsy.adoption.model.Adoption;
import com.awbd.pawsy.pet.dto.PetMapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = PetMapper.class)
public interface AdoptionMapper {
    @Mapping(target = "adopterName", source = "adopter.username")
    AdoptionSummary toSummary(Adoption adoption);
}
