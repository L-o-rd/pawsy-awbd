package com.awbd.pawsy.dto;

public record ShelterResponse(
   Long id,
   String name,
   String location,
   String email,
   String phone,
   String manager
) {}
