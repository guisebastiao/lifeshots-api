package com.lifeshots.lifeshotsapi.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record SubscribeWebPushRequestDTO(
        @NotBlank String endpoint,
        String expirationTime,
        @Valid KeysDTO keys
){
    public record KeysDTO(
            @NotBlank String p256dh,
            @NotBlank String auth
    ) {}
}
