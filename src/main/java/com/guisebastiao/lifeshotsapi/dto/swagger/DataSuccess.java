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
@Schema(name = "DataSuccess", description = "Successful API response with payload.")
public class DataSuccess<T> {

    @Schema(description = "Response status", example = "success", allowableValues = {"success"})
    private String status = "success";

    @Schema(description = "Payload returned by the operation")
    private T data;

    public static <T> DataSuccess<T> of(T data) {
        return new DataSuccess<>("success", data);
    }
}
