-- -----------------------------------------------------------------------------
-- EXTENSIONS
-- -----------------------------------------------------------------------------

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- -----------------------------------------------------------------------------
-- ROLES
-- -----------------------------------------------------------------------------

CREATE TABLE roles
(
    id         UUID                     NOT NULL DEFAULT gen_random_uuid(),
    role_name  VARCHAR(50)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_role_name UNIQUE (role_name)
);

-- -----------------------------------------------------------------------------
-- USERS
-- -----------------------------------------------------------------------------

CREATE TABLE users
(
    id         UUID                     NOT NULL DEFAULT gen_random_uuid(),
    handle     VARCHAR(50)              NOT NULL,
    email      VARCHAR(250)             NOT NULL,
    password   VARCHAR(80)              NOT NULL,
    is_deleted BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_handle UNIQUE (handle),
    CONSTRAINT uq_users_email UNIQUE (email)
);

-- -----------------------------------------------------------------------------
-- USERS_ROLES  (join table)
-- -----------------------------------------------------------------------------

CREATE TABLE users_roles
(
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    CONSTRAINT pk_users_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_users_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_users_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- PROFILES
-- -----------------------------------------------------------------------------

CREATE TABLE profiles
(
    id              UUID                     NOT NULL,
    full_name       VARCHAR(250)             NOT NULL,
    bio             VARCHAR(300),
    posts_count     INTEGER                  NOT NULL DEFAULT 0,
    followers_count INTEGER                  NOT NULL DEFAULT 0,
    following_count INTEGER                  NOT NULL DEFAULT 0,
    is_private      BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_profiles PRIMARY KEY (id),
    CONSTRAINT fk_profiles_user FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_profiles_posts_count CHECK (posts_count >= 0),
    CONSTRAINT ck_profiles_followers_count CHECK (followers_count >= 0),
    CONSTRAINT ck_profiles_following_count CHECK (following_count >= 0)
);

-- -----------------------------------------------------------------------------
-- PROFILE_PICTURES
-- -----------------------------------------------------------------------------

CREATE TABLE profile_pictures
(
    id         UUID                     NOT NULL,
    file_key   VARCHAR(255)             NOT NULL,
    file_name  VARCHAR(255)             NOT NULL,
    mime_type  VARCHAR(30)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_profile_pictures PRIMARY KEY (id),
    CONSTRAINT uq_profile_pictures_file_key UNIQUE (file_key),
    CONSTRAINT fk_profile_pictures_profile FOREIGN KEY (id) REFERENCES profiles (id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- NOTIFICATION_SETTINGS
-- -----------------------------------------------------------------------------

CREATE TABLE notification_settings
(
    id                        UUID                     NOT NULL,
    notify_all_notifications  BOOLEAN                  NOT NULL DEFAULT TRUE,
    notify_like_post          BOOLEAN                  NOT NULL DEFAULT TRUE,
    notify_like_comment       BOOLEAN                  NOT NULL DEFAULT TRUE,
    notify_like_reply_comment BOOLEAN                  NOT NULL DEFAULT TRUE,
    notify_like_story         BOOLEAN                  NOT NULL DEFAULT TRUE,
    notify_new_follower       BOOLEAN                  NOT NULL DEFAULT TRUE,
    notify_comment_post       BOOLEAN                  NOT NULL DEFAULT TRUE,
    notify_reply_comment      BOOLEAN                  NOT NULL DEFAULT TRUE,
    created_at                TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at                TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_notification_settings PRIMARY KEY (id),
    CONSTRAINT fk_notification_settings_user FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- PUSH_SUBSCRIPTIONS
-- -----------------------------------------------------------------------------

CREATE TABLE push_subscriptions
(
    id           UUID                     NOT NULL DEFAULT gen_random_uuid(),
    user_id      UUID                     NOT NULL,
    endpoint     VARCHAR(1000)            NOT NULL,
    p256dh       VARCHAR(500)             NOT NULL,
    auth         VARCHAR(500)             NOT NULL,
    user_agent   VARCHAR(500)             NOT NULL,
    device_id    VARCHAR(500)             NOT NULL,
    active       BOOLEAN                  NOT NULL DEFAULT TRUE,
    last_used_at TIMESTAMP WITH TIME ZONE,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_push_subscriptions PRIMARY KEY (id),
    CONSTRAINT fk_push_subscriptions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_push_subscriptions_user_id ON push_subscriptions (user_id);
CREATE INDEX idx_push_subscriptions_device_id ON push_subscriptions (device_id);

-- -----------------------------------------------------------------------------
-- RECOVER_PASSWORDS
-- -----------------------------------------------------------------------------

CREATE TABLE recover_passwords
(
    id         UUID                     NOT NULL DEFAULT gen_random_uuid(),
    user_id    UUID                     NOT NULL,
    token      VARCHAR(80)              NOT NULL,
    expires_at TIMESTAMP                NOT NULL,
    is_active  BOOLEAN                  NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_recover_passwords PRIMARY KEY (id),
    CONSTRAINT fk_recover_passwords_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_recover_passwords_user_id ON recover_passwords (user_id);
CREATE INDEX idx_recover_passwords_token ON recover_passwords (token);
CREATE INDEX idx_recover_passwords_active ON recover_passwords (is_active) WHERE is_active = TRUE;

-- -----------------------------------------------------------------------------
-- REFRESH_TOKENS
-- -----------------------------------------------------------------------------

CREATE TABLE refresh_tokens
(
    refresh_token UUID                     NOT NULL DEFAULT gen_random_uuid(),
    user_id       UUID                     NOT NULL,
    expires_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (refresh_token),
    CONSTRAINT uq_refresh_tokens_user UNIQUE (user_id),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- FOLLOWS
-- -----------------------------------------------------------------------------

CREATE TABLE follows
(
    follower_id  UUID                     NOT NULL,
    following_id UUID                     NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_follows PRIMARY KEY (follower_id, following_id),
    CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_follows_following FOREIGN KEY (following_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT ck_follows_no_self_follow CHECK (follower_id <> following_id)
);

CREATE INDEX idx_follows_following_id ON follows (following_id);

-- -----------------------------------------------------------------------------
-- POSTS
-- -----------------------------------------------------------------------------

CREATE TABLE posts
(
    id            UUID                     NOT NULL DEFAULT gen_random_uuid(),
    profile_id    UUID                     NOT NULL,
    content       VARCHAR(300)             NOT NULL,
    like_count    INTEGER                  NOT NULL DEFAULT 0,
    comment_count INTEGER                  NOT NULL DEFAULT 0,
    share_count   INTEGER                  NOT NULL DEFAULT 0,
    is_deleted    BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_posts PRIMARY KEY (id),
    CONSTRAINT fk_posts_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT ck_posts_like_count CHECK (like_count >= 0),
    CONSTRAINT ck_posts_comment_count CHECK (comment_count >= 0),
    CONSTRAINT ck_posts_share_count CHECK (share_count >= 0)
);

CREATE INDEX idx_posts_profile_id ON posts (profile_id);
CREATE INDEX idx_posts_created_at ON posts (created_at DESC);

-- -----------------------------------------------------------------------------
-- POST_PICTURES
-- -----------------------------------------------------------------------------

CREATE TABLE post_pictures
(
    id         UUID                     NOT NULL DEFAULT gen_random_uuid(),
    post_id    UUID                     NOT NULL,
    file_key   VARCHAR(255)             NOT NULL,
    file_name  VARCHAR(255)             NOT NULL,
    mime_type  VARCHAR(30)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_post_pictures PRIMARY KEY (id),
    CONSTRAINT uq_post_pictures_file_key UNIQUE (file_key),
    CONSTRAINT fk_post_pictures_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);

CREATE INDEX idx_post_pictures_post_id ON post_pictures (post_id);

-- -----------------------------------------------------------------------------
-- STORIES
-- -----------------------------------------------------------------------------

CREATE TABLE stories
(
    id         UUID                     NOT NULL DEFAULT gen_random_uuid(),
    profile_id UUID                     NOT NULL,
    caption    VARCHAR(150),
    like_count INTEGER                  NOT NULL DEFAULT 0,
    is_expired BOOLEAN                  NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN                  NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_stories PRIMARY KEY (id),
    CONSTRAINT fk_stories_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT ck_stories_like_count CHECK (like_count >= 0)
);

CREATE INDEX idx_stories_profile_id ON stories (profile_id);
CREATE INDEX idx_stories_expires_at ON stories (expires_at) WHERE is_expired = FALSE AND is_deleted = FALSE;

-- -----------------------------------------------------------------------------
-- STORY_PICTURES
-- -----------------------------------------------------------------------------

CREATE TABLE story_pictures
(
    id         UUID                     NOT NULL,
    file_key   VARCHAR(255)             NOT NULL,
    file_name  VARCHAR(255)             NOT NULL,
    mime_type  VARCHAR(30)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_story_pictures PRIMARY KEY (id),
    CONSTRAINT uq_story_pictures_file_key UNIQUE (file_key),
    CONSTRAINT fk_story_pictures_story FOREIGN KEY (id) REFERENCES stories (id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- COMMENTS
-- -----------------------------------------------------------------------------

CREATE TABLE comments
(
    id                  UUID                     NOT NULL DEFAULT gen_random_uuid(),
    profile_id          UUID                     NOT NULL,
    post_id             UUID                     NOT NULL,
    content             VARCHAR(300)             NOT NULL,
    like_count          INTEGER                  NOT NULL DEFAULT 0,
    reply_comment_count INTEGER                  NOT NULL DEFAULT 0,
    is_deleted          BOOLEAN                  NOT NULL DEFAULT FALSE,
    is_removed          BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT ck_comments_like_count CHECK (like_count >= 0),
    CONSTRAINT ck_comments_reply_comment_count CHECK (reply_comment_count >= 0)
);

CREATE INDEX idx_comments_post_id ON comments (post_id);
CREATE INDEX idx_comments_profile_id ON comments (profile_id);
CREATE INDEX idx_comments_created_at ON comments (created_at DESC);

-- -----------------------------------------------------------------------------
-- REPLY_COMMENTS
-- -----------------------------------------------------------------------------

CREATE TABLE reply_comments
(
    id         UUID                     NOT NULL DEFAULT gen_random_uuid(),
    profile_id UUID                     NOT NULL,
    comment_id UUID                     NOT NULL,
    content    VARCHAR(300)             NOT NULL,
    like_count INTEGER                  NOT NULL DEFAULT 0,
    is_deleted BOOLEAN                  NOT NULL DEFAULT FALSE,
    is_removed BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_reply_comments PRIMARY KEY (id),
    CONSTRAINT fk_reply_comments_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_reply_comments_comment FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE,
    CONSTRAINT ck_reply_comments_like_count CHECK (like_count >= 0)
);

CREATE INDEX idx_reply_comments_comment_id ON reply_comments (comment_id);
CREATE INDEX idx_reply_comments_profile_id ON reply_comments (profile_id);
CREATE INDEX idx_reply_comments_created_at ON reply_comments (created_at DESC);

-- -----------------------------------------------------------------------------
-- LIKE_POSTS
-- -----------------------------------------------------------------------------

CREATE TABLE like_posts
(
    profile_id UUID                     NOT NULL,
    post_id    UUID                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_like_posts PRIMARY KEY (profile_id, post_id),
    CONSTRAINT fk_like_posts_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_like_posts_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);

CREATE INDEX idx_like_posts_post_id ON like_posts (post_id);

-- -----------------------------------------------------------------------------
-- LIKE_COMMENTS
-- -----------------------------------------------------------------------------

CREATE TABLE like_comments
(
    profile_id UUID                     NOT NULL,
    comment_id UUID                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_like_comments PRIMARY KEY (profile_id, comment_id),
    CONSTRAINT fk_like_comments_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_like_comments_comment FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE
);

CREATE INDEX idx_like_comments_comment_id ON like_comments (comment_id);

-- -----------------------------------------------------------------------------
-- LIKE_REPLY_COMMENTS
-- -----------------------------------------------------------------------------

CREATE TABLE like_reply_comments
(
    profile_id       UUID                     NOT NULL,
    reply_comment_id UUID                     NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_like_reply_comments PRIMARY KEY (profile_id, reply_comment_id),
    CONSTRAINT fk_like_reply_comments_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_like_reply_comments_reply_comment FOREIGN KEY (reply_comment_id) REFERENCES reply_comments (id) ON DELETE CASCADE
);

CREATE INDEX idx_like_reply_comments_reply_comment_id ON like_reply_comments (reply_comment_id);

-- -----------------------------------------------------------------------------
-- LIKE_STORIES
-- -----------------------------------------------------------------------------

CREATE TABLE like_stories
(
    profile_id UUID                     NOT NULL,
    story_id   UUID                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_like_stories PRIMARY KEY (profile_id, story_id),
    CONSTRAINT fk_like_stories_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_like_stories_story FOREIGN KEY (story_id) REFERENCES stories (id) ON DELETE CASCADE
);

CREATE INDEX idx_like_stories_story_id ON like_stories (story_id);

-- -----------------------------------------------------------------------------
-- NOTIFICATIONS
-- -----------------------------------------------------------------------------

CREATE TYPE notification_type AS ENUM (
    'LIKE_POST',
    'LIKE_COMMENT',
    'LIKE_REPLY_COMMENT',
    'LIKE_STORY'
    'NEW_FOLLOWER',
    'COMMENT_POST',
    'REPLY_COMMENT'
);

CREATE TABLE notifications
(
    id          UUID                     NOT NULL DEFAULT gen_random_uuid(),
    sender_id   UUID                     NOT NULL,
    receiver_id UUID                     NOT NULL,
    title       VARCHAR(255)             NOT NULL,
    message     TEXT                     NOT NULL,
    type        notification_type,
    read        BOOLEAN                  NOT NULL DEFAULT FALSE,
    is_deleted  BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notifications_sender FOREIGN KEY (sender_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_receiver FOREIGN KEY (receiver_id) REFERENCES profiles (id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_receiver_id ON notifications (receiver_id);
CREATE INDEX idx_notifications_sender_id ON notifications (sender_id);
CREATE INDEX idx_notifications_unread ON notifications (receiver_id, read) WHERE read = FALSE AND is_deleted = FALSE;
CREATE INDEX idx_notifications_created_at ON notifications (created_at DESC);