package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record PushSubscriptionRequest(
        @NotBlank(message = "Informe o endpoint")
        String endpoint,

        @NotEmpty(message = "As chaves são obrigatórias")
        Map<String, String> keys
) { }
