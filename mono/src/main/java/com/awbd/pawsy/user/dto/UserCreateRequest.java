package com.awbd.pawsy.user.dto;

import jakarta.validation.constraints.*;

public record UserCreateRequest(
    @NotBlank(message = "Users should have a username.")
    @Size(min = 3, max = 20, message = "Username must be 3–20 characters.")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Username can contain letters, numbers, ., _, - only.")
    String username,

    @NotBlank(message = "Users should have a password.")
    @Size(min = 8, max = 64, message = "Password must be 8–64 characters.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number.")
    String password,

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

    @NotBlank(message = "Users should have an e-mail.")
    @Email(message = "Invalid e-mail format.")
    String email,

    @NotBlank(message = "Users should have a phone number.")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be 10–15 digits, optional +.")
    String phone
) {}
