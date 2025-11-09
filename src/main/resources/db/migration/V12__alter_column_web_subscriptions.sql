ALTER TABLE push_subscriptions
DROP COLUMN endpoint,
DROP COLUMN p256dh,
DROP COLUMN auth;

ALTER TABLE push_subscriptions ADD COLUMN token VARCHAR(255) NOT NULL;
