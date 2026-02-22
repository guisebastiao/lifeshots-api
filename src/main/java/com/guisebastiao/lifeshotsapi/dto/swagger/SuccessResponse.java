package com.guisebastiao.lifeshotsapi.dto.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SuccessResponse", description = "Successful API response without payload.")
public class SuccessResponse {

    @Schema(description = "Response status", example = "success", allowableValues = {"success"})
    private final String status = "success";

    public static SuccessResponse success() {
        return new SuccessResponse();
    }
}

