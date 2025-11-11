package com.guisebastiao.lifeshotsapi.dto.request;

public record NotificationSettingRequest(
        Boolean notifyCommentOnPost,
        Boolean notifyLikeInComment,
        Boolean notifyLikeInCommentReply,
        Boolean notifyNewFollowers,
        Boolean notifyLikeInStory
) { }
