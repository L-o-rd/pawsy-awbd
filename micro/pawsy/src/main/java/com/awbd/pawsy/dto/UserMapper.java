package com.awbd.pawsy.dto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserUpdateRequest toUpdateRequest(UserResponse user);
}
