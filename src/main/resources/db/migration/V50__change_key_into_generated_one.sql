ALTER TABLE done_exercise_register
    DROP CONSTRAINT fk_done_training_register;
ALTER TABLE done_exercise_register
    DROP COLUMN routine_id;
ALTER TABLE done_exercise_register
    DROP COLUMN training_id;
ALTER TABLE done_training_register
    DROP CONSTRAINT done_training_register_pkey;
ALTER TABLE done_training_register
    ADD COLUMN id SERIAL PRIMARY KEY;
ALTER TABLE done_exercise_register
    ADD COLUMN done_training_register_id INTEGER;
ALTER TABLE done_exercise_register
    ADD CONSTRAINT id_done_training_register_id_fkey
        FOREIGN KEY (done_training_register_id)
            REFERENCES done_training_register (id);