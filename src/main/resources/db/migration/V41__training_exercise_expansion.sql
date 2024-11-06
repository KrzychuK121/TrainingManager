ALTER TABLE training_exercise
    ADD COLUMN id SERIAL PRIMARY KEY;

ALTER TABLE training_exercise
    ADD COLUMN parameters_id INTEGER
        REFERENCES exercise_parameters (id);

UPDATE training_exercise
SET parameters_id = exercise_id;

ALTER TABLE training_exercise
    ALTER COLUMN parameters_id
        SET NOT NULL;
