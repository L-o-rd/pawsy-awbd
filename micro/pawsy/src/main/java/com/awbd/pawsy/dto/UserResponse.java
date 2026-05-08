package com.awbd.pawsy.dto;

import java.util.Set;

public record UserResponse(
    Long id,
    String username,
    String password,
    String lastName,
    String firstName,
    String email,
    String phone,
    Set<RoleResponse> roles,
    Boolean accountNonExpired,
    Boolean accountNonLocked,
    Boolean credentialsNonExpired,
    Boolean enabled
) {}
