package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record DeleteRequest(
        @NotNull
        @NotEmpty
        List<UUID> ids
) { }
