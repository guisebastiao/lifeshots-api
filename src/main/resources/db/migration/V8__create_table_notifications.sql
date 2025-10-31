CREATE TYPE notification_type AS ENUM (
  'LIKE_IN_POST',
  'COMMENT_ON_POST',
  'LIKE_IN_COMMENT',
  'LIKE_IN_COMMENT_REPLY',
  'NEW_FOLLOWERS',
  'LIKE_IN_STORY'
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    receiver_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    body VARCHAR(255) NOT NULL,
    type notification_type NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);