package com.awbd.pawsy.dto;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    Integer number,
    Integer size,
    Long totalElements,
    Integer totalPages,
    Boolean first,
    Boolean last
) {
    public static <T> PageResponse<T> empty() {
        return new PageResponse<>(List.of(), 0, 0, 0L, 0, false, false);
    }
}
