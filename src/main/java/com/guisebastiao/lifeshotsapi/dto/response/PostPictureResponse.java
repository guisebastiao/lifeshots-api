package com.guisebastiao.lifeshotsapi.dto.response;

import java.util.UUID;

public record PostPictureResponse(
        UUID id,
        String fileKey,
        String mimeType,
        String url
) { }
