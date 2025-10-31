package com.guisebastiao.lifeshotsapi.dto;

public record Paging(
        long totalItems,
        long totalPages,
        long currentPage,
        long itemsPerPage
) { }
