-- Notes can be written as Markdown (stored as the raw Markdown source) or as rich text (HTML).
-- Existing notes were all written with the rich-text editor.
ALTER TABLE item_note ADD COLUMN format TEXT NOT NULL DEFAULT 'HTML' CHECK (format IN ('HTML', 'MARKDOWN'));
