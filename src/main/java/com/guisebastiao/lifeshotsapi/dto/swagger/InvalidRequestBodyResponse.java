package com.guisebastiao.lifeshotsapi.dto.swagger;

import com.guisebastiao.lifeshotsapi.dto.response.FieldErrorResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "InvalidRequestBodyResponse", description = "Standard error response for invalid request body")
public class InvalidRequestBodyResponse {

    @Schema(description = "Response status", example = "error", allowableValues = {"error"})
    private String status = "error";

    @Schema(description = "Error information")
    private ValidationErrorDetails error;

    public static InvalidRequestBodyResponse of(String code, String message, List<FieldErrorResponse> fieldErrors) {
        return new InvalidRequestBodyResponse("error", new ValidationErrorDetails(code, message, fieldErrors));
    }

    @Schema(name = "ValidationErrorDetails", description = "Validation error details")
    public record ValidationErrorDetails(
            String code,
            String message,
            @ArraySchema(schema = @Schema(implementation = FieldErrorResponse.class))
            List<FieldErrorResponse> details
    ) { }
}