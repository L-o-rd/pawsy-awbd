package com.awbd.pawsy.pet.dto;

import com.awbd.pawsy.pet.model.Pet;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PetMapper {

    String DEFAULT_PHOTO = "/images/404.png";

    @Mapping(target = "species", source = "species")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "sex", source = "sex")
    @Mapping(target = "shelterId", source = "shelter.id")
    @Mapping(target = "photo", expression = "java(resolvePhoto(pet.getPhoto()))")
    PetSummary toSummary(Pet pet);

    default String resolvePhoto(String photoUrl) {
        return (photoUrl == null || photoUrl.isBlank()) ? DEFAULT_PHOTO : photoUrl;
    }

}
