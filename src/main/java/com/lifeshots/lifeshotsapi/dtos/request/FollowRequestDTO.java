package com.lifeshots.lifeshotsapi.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record FollowRequestDTO(
        @NotBlank(message = "Informe o id do usuário")
        String userId
) { }
