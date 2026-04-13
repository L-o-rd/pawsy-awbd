package com.awbd.pawsy.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    String username,
    String email,

    @NotBlank(message = "Users should have a first name.")
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "User first name can only contain letters, spaces, and .'-")
    @Size(min = 2, max = 20, message = "User first name must be between 2 and 20 characters.")
    String firstName,

    @NotBlank(message = "Users should have a last name.")
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "User last name can only contain letters, spaces, and .'-")
    @Size(min = 2, max = 20, message = "User last name must be between 2 and 20 characters.")
    String lastName,

    @NotBlank(message = "Users should have a phone number.")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be 10–15 digits, optional +.")
    String phone
) {}
