package com.awbd.pawsy.pet.dto;

import com.awbd.pawsy.pet.model.Review;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "username", source = "adopter.username")
    ReviewSummary toSummary(Review review);
}
