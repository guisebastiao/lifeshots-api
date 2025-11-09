package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikeCommentRequest(
        @NotNull(message = "Informe o valor do like")
        boolean like
) { }
