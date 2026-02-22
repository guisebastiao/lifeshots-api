package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "notification_settings")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class NotificationSetting extends Auditable {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", nullable = false, unique = true)
    private User user;

    @Column(name = "notify_all_notifications", nullable = false)
    private boolean notifyAllNotifications = true;

    @Column(name = "notify_like_post", nullable = false)
    private boolean notifyLikePost = true;

    @Column(name = "notify_like_comment", nullable = false)
    private boolean notifyLikeComment = true;

    @Column(name = "notify_like_reply_comment", nullable = false)
    private boolean notifyLikeReplyComment = true;

    @Column(name = "notify_like_story", nullable = false)
    private boolean notifyLikeStory = true;

    @Column(name = "notify_new_follower", nullable = false)
    private boolean notifyNewFollower = true;

    @Column(name = "notify_comment_post", nullable = false)
    private boolean notifyCommentPost = true;

    @Column(name = "notify_reply_comment", nullable = false)
    private boolean notifyReplyComment = true;

    public void disableAllNotifications() {
        this.notifyAllNotifications = false;
        this.notifyLikePost = false;
        this.notifyLikeComment = false;
        this.notifyLikeReplyComment = false;
        this.notifyLikeStory = false;
        this.notifyNewFollower = false;
        this.notifyCommentPost = false;
        this.notifyReplyComment = false;
    }

    public void enableAllNotifications() {
        this.notifyAllNotifications = true;
        this.notifyLikePost = true;
        this.notifyLikeComment = true;
        this.notifyLikeReplyComment = true;
        this.notifyLikeStory = true;
        this.notifyNewFollower = true;
        this.notifyCommentPost = true;
        this.notifyReplyComment = true;
    }
}
