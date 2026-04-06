package com.guisebastiao.lifeshotsapi.dto.response;

public record NotificationSettingResponse(
        boolean notifyAllNotifications,
        boolean notifyLikePost,
        boolean notifyLikeComment,
        boolean notifyLikeReplyComment,
        boolean notifyLikeStory,
        boolean notifyNewFollower,
        boolean notifyCommentPost,
        boolean notifyReplyComment
) { }
