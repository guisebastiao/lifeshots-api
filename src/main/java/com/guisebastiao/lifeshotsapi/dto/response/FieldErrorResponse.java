package com.guisebastiao.lifeshotsapi.dto.response;

public record FieldErrorResponse(
        String field,
        String error
) { }
