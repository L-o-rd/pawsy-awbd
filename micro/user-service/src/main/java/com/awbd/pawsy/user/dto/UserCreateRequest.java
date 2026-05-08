package com.awbd.pawsy.user.dto;

public record UserCreateRequest(
    String username,
    String password,
    String firstName,
    String lastName,
    String email,
    String phone
) {}