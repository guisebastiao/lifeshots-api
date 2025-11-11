package com.guisebastiao.lifeshotsapi.dto.response;

public record NotificationSettingResponse(
        boolean notifyAllNotifications,
        boolean notifyCommentOnPost,
        boolean notifyLikeInComment,
        boolean notifyLikeInCommentReply,
        boolean notifyNewFollowers,
        boolean notifyLikeInStory
) { }
