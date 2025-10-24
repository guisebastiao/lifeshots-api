package com.guisebastiao.lifeshotsapi.dto;

public record FieldErrorResponse(
        String field,
        String error
) { }
