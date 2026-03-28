package com.guisebastiao.lifeshotsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    LIKE_POST("LIKE_POST"),
    LIKE_COMMENT("LIKE_COMMENT"),
    LIKE_REPLY_COMMENT("LIKE_REPLY_COMMENT"),
    LIKE_STORY("LIKE_STORY"),
    NEW_FOLLOWER("NEW_FOLLOWER"),
    COMMENT_POST("COMMENT_POST"),
    REPLY_COMMENT("REPLY_COMMENT");

    private final String value;
}
