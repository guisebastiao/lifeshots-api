package com.guisebastiao.lifeshotsapi.dto.request;


import org.hibernate.validator.constraints.Length;

public record StoryUpdateRequest(
        @Length(max = 150)
        String caption
) { }
