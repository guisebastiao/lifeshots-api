package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record NotificationSettingRequest(
        Boolean notifyLikePost,
        Boolean notifyLikeComment,
        Boolean notifyLikeReplyComment,
        Boolean notifyLikeStory,
        Boolean notifyNewFollower,
        Boolean notifyCommentPost,
        Boolean notifyReplyComment
) {
    public record NotifyAll(

            @NotNull
            boolean notifyAllNotifications
    ) { }

}
