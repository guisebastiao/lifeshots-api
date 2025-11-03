package com.guisebastiao.lifeshotsapi.dto.response;

import java.util.UUID;

public record ProfilePictureResponse(
        UUID id,
        String fileKey,
        String mimeType,
        String url
) { }
