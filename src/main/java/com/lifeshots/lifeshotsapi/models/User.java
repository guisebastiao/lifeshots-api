package com.lifeshots.lifeshotsapi.models;

import com.lifeshots.lifeshotsapi.utils.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends Auditable implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 50, unique = true, nullable = false)
    private String nickname;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String surname;

    @Column(length = 50, unique = true, nullable = false)
    private String email;

    @Column(length = 60, nullable = false)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "amount_following", nullable = false)
    private Integer amountFollowing = 0;

    @Column(name = "amount_followers", nullable = false)
    private Integer amountFollowers = 0;

    @Column(name = "amount_posts", nullable = false)
    private Integer amountPosts = 0;

    @Column(name = "private_account", nullable = false)
    private Boolean privateAccount = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ProfilePicture profilePicture;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ResetPassword resetPassword;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Notification> senders;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Notification> receivers;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Setting setting;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Story> stories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CommentPost> commentPosts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CommentReply> commentReplies;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LikeCommentPost> likeCommentPosts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LikePost> likePosts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LikeStory> likeStories;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follow> followers;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Follow> following;

    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL)
    private List<Block> blocker;

    @OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL)
    private List<Block> blocked;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PushSubscription> pushSubscriptions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
