DROP TABLE users_trainings;

ALTER TABLE training
    ADD COLUMN owner_id VARCHAR
        REFERENCES identity_user (id);

UPDATE training t
SET owner_id = (SELECT u.id
                FROM identity_user u
                WHERE u.username = 'uzytkownik')
WHERE t.title IN ('Klatka piersiowa', 'Trening brzucha');