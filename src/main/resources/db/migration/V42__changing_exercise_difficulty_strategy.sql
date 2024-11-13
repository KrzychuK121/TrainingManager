/*
This migration creates new difficulty system logic where exercise now contains
default burned calories value which will be handled by backend logic (multiplied
by series/repetitions etc.) and the backend will contain logic to decide how to
interpret this value based on user input/data (e.g. BMI).
*/

ALTER TABLE exercise_parameters
    DROP COLUMN difficulty;

ALTER TABLE exercise
    ADD COLUMN default_burned_kcal integer;

UPDATE exercise
SET default_burned_kcal = CASE
    WHEN id = 1 THEN 4
    WHEN id = 2 THEN 6
    WHEN id = 3 THEN 4
    WHEN id = 4 THEN 1
    ELSE 1
    END
WHERE id IN (1, 2, 3, 4);

ALTER TABLE exercise
    ALTER COLUMN default_burned_kcal
        SET NOT NULL;