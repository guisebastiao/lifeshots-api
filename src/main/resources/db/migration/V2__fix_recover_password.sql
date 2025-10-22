CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================
-- Function to update updated_at
-- =========================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

-- =========================
-- USERS
-- =========================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(250) NOT NULL UNIQUE,
    password VARCHAR(80) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_timestamp_users BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- RECOVER PASSWORDS
-- =========================
CREATE TABLE recover_passwords (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(80) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_timestamp_recover_passwords BEFORE UPDATE ON recover_passwords FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- PROFILES
-- =========================
CREATE TABLE profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(250) NOT NULL,
    bio VARCHAR(300),
    posts_count INT NOT NULL DEFAULT 0,
    followers_count INT NOT NULL DEFAULT 0,
    following_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_timestamp_profiles BEFORE UPDATE ON profiles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- PROFILE PICTURES
-- =========================
CREATE TABLE profile_pictures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id UUID NOT NULL UNIQUE REFERENCES profiles(id) ON DELETE CASCADE,
    file_key VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    mime_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_timestamp_profile_pictures BEFORE UPDATE ON profile_pictures FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- FOLLOWS
-- =========================
CREATE TABLE follows (
    follower_id UUID NOT NULL,
    following_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_follows PRIMARY KEY (follower_id, following_id),
    CONSTRAINT fk_follower_profiles FOREIGN KEY (follower_id) REFERENCES profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_following_profiles FOREIGN KEY (following_id) REFERENCES profiles(id) ON DELETE CASCADE,
    CONSTRAINT chk_no_self_follow CHECK (follower_id <> following_id)
);

CREATE TRIGGER set_timestamp_follows BEFORE UPDATE ON follows FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- STORIES
-- =========================
CREATE TABLE stories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    caption VARCHAR(150),
    like_count INT NOT NULL DEFAULT 0,
    is_expired BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_timestamp_stories BEFORE UPDATE ON stories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- STORY PICTURES
-- =========================
CREATE TABLE story_pictures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    story_id UUID NOT NULL REFERENCES stories(id) ON DELETE CASCADE,
    file_key VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    mime_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_timestamp_story_pictures BEFORE UPDATE ON story_pictures FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- LIKE STORIES
-- =========================
CREATE TABLE like_stories (
    profile_id UUID NOT NULL,
    story_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_like_stories PRIMARY KEY (profile_id, story_id),
    CONSTRAINT fk_like_stories_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_like_stories_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE
);

CREATE TRIGGER set_timestamp_like_stories BEFORE UPDATE ON like_stories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- POSTS
-- =========================
CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    content VARCHAR(300) NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    share_count INT NOT NULL DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_timestamp_posts BEFORE UPDATE ON posts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- POST PICTURES
-- =========================
CREATE TABLE post_pictures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    file_key VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    mime_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_timestamp_post_pictures BEFORE UPDATE ON post_pictures FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- LIKE POSTS
-- =========================
CREATE TABLE like_posts (
    profile_id UUID NOT NULL,
    post_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_like_posts PRIMARY KEY (profile_id, post_id),
    CONSTRAINT fk_like_posts_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_like_posts_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE TRIGGER set_timestamp_like_posts BEFORE UPDATE ON like_posts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- COMMENTS
-- =========================
CREATE TABLE comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    content VARCHAR(300) NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    reply_comment_count INT NOT NULL DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_timestamp_comments BEFORE UPDATE ON comments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- LIKE COMMENTS
-- =========================
CREATE TABLE like_comments (
    profile_id UUID NOT NULL,
    comment_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_like_comments PRIMARY KEY (profile_id, comment_id),
    CONSTRAINT fk_like_comments_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_like_comments_comment FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);

CREATE TRIGGER set_timestamp_like_comments BEFORE UPDATE ON like_comments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- REPLY COMMENTS
-- =========================
CREATE TABLE reply_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    comment_id UUID NOT NULL REFERENCES comments(id) ON DELETE CASCADE,
    content VARCHAR(300) NOT NULL,
    like_count INT NOT NULL DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER set_timestamp_reply_comments BEFORE UPDATE ON reply_comments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================
-- LIKE REPLY COMMENTS
-- =========================
CREATE TABLE like_reply_comments (
    profile_id UUID NOT NULL,
    reply_comment_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_like_reply_comments PRIMARY KEY (profile_id, reply_comment_id),
    CONSTRAINT fk_like_reply_comments_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_like_reply_comments_reply FOREIGN KEY (reply_comment_id) REFERENCES reply_comments(id) ON DELETE CASCADE
);

CREATE TRIGGER set_timestamp_like_reply_comments BEFORE UPDATE ON like_reply_comments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
