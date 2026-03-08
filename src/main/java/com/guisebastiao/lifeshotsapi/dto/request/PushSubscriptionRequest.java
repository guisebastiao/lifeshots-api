package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record PushSubscriptionRequest(

        @NotBlank(message = "{validation.push-subscription-request.endpoint.not-blank}")
        String endpoint,

        @Valid
        Keys keys
) {
    public record Keys(

            @NotBlank(message = "{validation.push-subscription-request.keys.p256dh.not-blank}")
            String p256dh,

            @NotBlank(message = "{validation.push-subscription-request.keys.auth.not-blank}")
            String auth
    ){}
}
