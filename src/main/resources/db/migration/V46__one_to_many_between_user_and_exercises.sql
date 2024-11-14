DROP TABLE users_exercises;

ALTER TABLE exercise
    ADD COLUMN owner_id VARCHAR
        REFERENCES identity_user (id);

UPDATE exercise e
SET owner_id = (SELECT u.id
                FROM identity_user u
                WHERE u.username = 'uzytkownik');
