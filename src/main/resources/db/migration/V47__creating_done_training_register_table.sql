/*
This migration creates table that contains data for finished trainings by users.
When they create routine, make them active and then finished its training,
these data will be stored here (finished exercise statistics will be stored
in separate table).
*/

CREATE TABLE done_training_register
(
    routine_id  INTEGER NOT NULL REFERENCES training_routine (id),
    training_id INTEGER NOT NULL REFERENCES training (id),
    start_date  timestamp,
    end_date    timestamp,
    PRIMARY KEY (routine_id, training_id)
);