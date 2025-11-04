ALTER TABLE story_pictures DROP CONSTRAINT IF EXISTS fkq76srkgmu5r9j7qtyuy3e9430;

ALTER TABLE story_pictures DROP COLUMN IF EXISTS story_id;

ALTER TABLE story_pictures ALTER COLUMN id DROP DEFAULT;

ALTER TABLE story_pictures ADD CONSTRAINT fk_story_picture_story FOREIGN KEY (id) REFERENCES stories(id) ON DELETE CASCADE;
