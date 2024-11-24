/*
This migration creates table which contains finished exercises statistics
associated with training register row.
*/
CREATE TABLE done_exercise_register
(
    id                   SERIAL PRIMARY KEY,
    routine_id           INTEGER NOT NULL,
    training_id          INTEGER NOT NULL,
    training_exercise_id INTEGER NOT NULL REFERENCES training_exercise (id),
    done_series          INTEGER NOT NULL,
    CONSTRAINT fk_done_training_register
        FOREIGN KEY (routine_id, training_id)
            REFERENCES done_training_register (routine_id, training_id)
);