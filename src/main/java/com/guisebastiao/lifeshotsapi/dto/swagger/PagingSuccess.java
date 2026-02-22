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
@Schema(name = "PagingSuccess", description = "Successful paginated API response.")
public class PagingSuccess<T> {

    @Schema(description = "Response status", example = "success", allowableValues = {"success"})
    private String status = "success";

    @Schema(description = "List of returned items")
    private T data;

    @Schema(description = "Pagination metadata")
    private Meta meta;

    public static <T> PagingSuccess<T> of(T data, Meta meta) {
        return new PagingSuccess<>("success", data, meta);
    }

    @Schema(description = "Pagination metadata")
    public record Meta(
            long totalItems,
            long totalPages,
            long currentPage,
            long itemsPerPage
    ) {}
}
