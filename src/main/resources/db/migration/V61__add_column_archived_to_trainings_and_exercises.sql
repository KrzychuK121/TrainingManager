ALTER TABLE training
    ADD COLUMN archived BOOLEAN DEFAULT false;
ALTER TABLE exercise
    ADD COLUMN archived BOOLEAN DEFAULT false;