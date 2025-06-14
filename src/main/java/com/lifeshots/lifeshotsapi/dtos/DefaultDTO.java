package com.lifeshots.lifeshotsapi.dtos;

public record DefaultDTO(
        String message,
        Boolean success,
        Object data,
        ErrorDTO error,
        PagingDTO paging
) { }
