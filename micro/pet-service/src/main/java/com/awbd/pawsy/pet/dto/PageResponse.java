package com.awbd.pawsy.pet.dto;

import org.springframework.data.domain.Page;

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
    public static <T> PageResponse<T> collect(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }
}
