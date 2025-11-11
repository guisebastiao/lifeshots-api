package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_settings")
public class NotificationSetting extends Auditable {

    @Id
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", nullable = false, unique = true)
    private User user;

    @Column(name = "notify_like_in_post", nullable = false)
    private boolean notifyAllNotifications = true;

    @Column(name = "notify_all_notifications", nullable = false)
    private boolean notifyLikeInPost = true;

    @Column(name = "notify_comment_on_post", nullable = false)
    private boolean notifyCommentOnPost = true;

    @Column(name = "notify_like_in_comment", nullable = false)
    private boolean notifyLikeInComment = true;

    @Column(name = "notify_like_in_comment_reply", nullable = false)
    private boolean notifyLikeInCommentReply = true;

    @Column(name = "notify_new_followers", nullable = false)
    private boolean notifyNewFollowers = true;

    @Column(name = "notify_like_in_story", nullable = false)
    private boolean notifyLikeInStory = true;

    public void disableAllNotifications() {
        this.notifyAllNotifications = false;
        this.notifyLikeInPost = false;
        this.notifyCommentOnPost = false;
        this.notifyLikeInComment = false;
        this.notifyLikeInCommentReply = false;
        this.notifyNewFollowers = false;
        this.notifyLikeInStory = false;
    }

    public void enableAllNotifications() {
        this.notifyAllNotifications = true;
        this.notifyLikeInPost = true;
        this.notifyCommentOnPost = true;
        this.notifyLikeInComment = true;
        this.notifyLikeInCommentReply = true;
        this.notifyNewFollowers = true;
        this.notifyLikeInStory = true;
    }
}
