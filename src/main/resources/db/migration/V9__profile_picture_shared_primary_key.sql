ALTER TABLE profile_pictures DROP CONSTRAINT IF EXISTS fk7tyoviljiyyhhk8in3s27k9bu;
ALTER TABLE profile_pictures DROP CONSTRAINT IF EXISTS uktewrl91m5lufjmmnp99q46rpj;
ALTER TABLE profile_pictures DROP COLUMN IF EXISTS profile_id;

ALTER TABLE profile_pictures ALTER COLUMN id DROP DEFAULT;

ALTER TABLE profile_pictures ADD CONSTRAINT fk_profile_picture_profile FOREIGN KEY (id) REFERENCES profiles(id) ON DELETE CASCADE;
