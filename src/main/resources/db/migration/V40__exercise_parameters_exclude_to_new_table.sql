CREATE TABLE exercise_parameters
(
    id         SERIAL PRIMARY KEY,
    repetition INTEGER NOT NULL,
    rounds     INTEGER NOT NULL,
    time       TIME(6),
    weights    SMALLINT,
    difficulty VARCHAR(30),
    CHECK ((repetition >= 0)
        AND (repetition <= 100)),
    CHECK ((rounds >= 1)
        AND (rounds <= 10)),
    CHECK ((weights >= 0)
        AND (weights <= 300))
);

INSERT INTO exercise_parameters (id, repetition, rounds, time, weights,
                                 difficulty)
SELECT ex.id, ex.repetition, ex.rounds, ex.time, ex.weights, ex.difficulty
FROM exercise ex;

ALTER TABLE exercise
    ADD COLUMN parameters_id INTEGER
        REFERENCES exercise_parameters (id);

UPDATE exercise
SET parameters_id = id;

ALTER TABLE exercise
    ALTER COLUMN parameters_id SET NOT NULL;

ALTER TABLE exercise
    DROP COLUMN IF EXISTS repetition,
    DROP COLUMN IF EXISTS rounds,
    DROP COLUMN IF EXISTS time,
    DROP COLUMN IF EXISTS weights,
    DROP COLUMN IF EXISTS difficulty;
