CREATE TABLE notification_settings (
    id UUID NOT NULL PRIMARY KEY,
    notify_like_in_post BOOLEAN NOT NULL DEFAULT true,
    notify_all_notifications BOOLEAN NOT NULL DEFAULT true,
    notify_comment_on_post BOOLEAN NOT NULL DEFAULT true,
    notify_like_in_comment BOOLEAN NOT NULL DEFAULT true,
    notify_like_in_comment_reply BOOLEAN NOT NULL DEFAULT true,
    notify_new_followers BOOLEAN NOT NULL DEFAULT true,
    notify_like_in_story BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_notification_settings_users FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TRIGGER set_timestamp_notification_settings BEFORE UPDATE ON notification_settings FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();