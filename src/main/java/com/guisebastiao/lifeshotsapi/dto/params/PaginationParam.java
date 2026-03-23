package com.guisebastiao.lifeshotsapi.dto.params;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaginationParam(

        @NotNull
        @Min(value = 1)
        Integer offset,

        @NotNull
        @Min(value = 1)
        @Max(value = 50)
        Integer limit
) {}
