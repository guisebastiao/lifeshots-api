package com.guisebastiao.lifeshotsapi.dto.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ErrorResponse", description = "Standard error response.")
public class ErrorResponse {

    @Schema(description = "Response status", example = "error", allowableValues = {"error"})
    private String status = "error";

    @Schema(description = "Error information")
    private ErrorDetails error;

    @Schema(name = "ErrorDetails", description = "Error details")
    public record ErrorDetails(
            String code,
            String message,
            Object details
    ) {}
}
