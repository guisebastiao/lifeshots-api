package com.guisebastiao.lifeshotsapi.dto.params;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaginationParam(

        @NotNull(message = "{validation.pagination-filter.offset.not-null}")
        @Min(value = 1, message = "{validation.pagination-filter.offset.min}")
        Integer offset,

        @NotNull(message = "{validation.pagination-filter.limit.not-null}")
        @Min(value = 1, message = "{validation.pagination-filter.limit.min}")
        @Max(value = 50, message = "{validation.pagination-filter.limit.max}")
        Integer limit
) {}
