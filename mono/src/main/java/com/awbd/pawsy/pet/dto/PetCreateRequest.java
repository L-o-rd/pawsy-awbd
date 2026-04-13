package com.awbd.pawsy.pet.dto;

import jakarta.validation.constraints.*;

public record PetCreateRequest(
    @NotBlank(message = "Pets should have a name.")
    @Pattern(
        regexp = "^[\\p{L} .'-]+$",
        message = "Pet name can only contain letters, spaces, and .'-")
    @Size(min = 2, max = 20, message = "Pet name must be between 2 and 20 characters.")
    String name,

    @NotBlank(message = "Pets should have a species.")
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "Pet species can only contain letters, spaces, and .'-")
    @Size(min = 2, max = 20, message = "Pet species must be between 2 and 20 characters.")
    String species,

    @NotNull
    @PositiveOrZero(message = "Pet age should be at least zero.")
    Integer age,

    @NotBlank(message = "Pets should have a description.")
    String description,

    @NotNull
    String sex,
    String photo
) {}
