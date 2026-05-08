package com.awbd.pawsy.user.dto;

import java.util.Set;

public record UserSummary(
    Long id,
    String username,
    String password,
    String lastName,
    String firstName,
    String email,
    String phone,
    Set<RoleSummary> roles,
    Boolean accountNonExpired,
    Boolean accountNonLocked,
    Boolean credentialsNonExpired,
    Boolean enabled
) {}
