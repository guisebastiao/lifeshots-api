package com.lifeshots.lifeshotsapi.dtos;

public record PagingDTO(
        long totalItems,
        long totalPages,
        long currentPage,
        long itemsPerPage
) { }
