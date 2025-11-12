package com.guisebastiao.lifeshotsapi.dto.response;

import java.util.List;

public record StoryFeedResponse(
        ProfileResponse profile,
        List<StoryItemResponse> stories
) { }
