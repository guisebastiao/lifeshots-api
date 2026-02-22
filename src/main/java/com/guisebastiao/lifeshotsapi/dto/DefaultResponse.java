package com.guisebastiao.lifeshotsapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultResponse<T> {
    private Status status;
    private T data;
    private Meta meta;
    private ErrorDetails error;

    public static DefaultResponse<Void> success() {
        return new DefaultResponse<>(Status.SUCCESS, null, null, null);
    }

    public static <T> DefaultResponse<T> success(T data) {
        return new DefaultResponse<>(Status.SUCCESS, data, null, null);
    }

    public static <T> DefaultResponse<T> success(T data, Meta meta) {
        return new DefaultResponse<>(Status.SUCCESS, data, meta, null);
    }

    public static DefaultResponse<Void> error(String code, String message) {
        return new DefaultResponse<>(Status.ERROR, null, null, new ErrorDetails(code, message, null));
    }

    public static DefaultResponse<Void> error(String code, String message, Object details) {
        return new DefaultResponse<>(Status.ERROR, null, null, new ErrorDetails(code, message, details));
    }

    public enum Status {
        SUCCESS("success"),
        ERROR("error");

        @Getter
        @JsonValue
        private final String value;

        Status(String value) {
            this.value = value;
        }
    }

    @Builder
    public record Meta(
            long totalItems,
            long totalPages,
            long currentPage,
            long itemsPerPage
    ) {}

    public record ErrorDetails(
            String code,
            String message,
            Object details
    ) {}
}

