package com.guisebastiao.lifeshotsapi.dto;

public record DefaultResponse<T>(
        boolean success,
        String message,
        T data
) { }
