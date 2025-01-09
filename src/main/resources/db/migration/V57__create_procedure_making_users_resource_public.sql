/*
This migration is responsible for creating procedure which will make every training
and exercise of specified user to be public.
*/
DROP PROCEDURE IF EXISTS make_resources_public_for;
CREATE PROCEDURE make_resources_public_for(IN owner_id_to_clear varchar)
    LANGUAGE plpgsql
AS
$$
BEGIN
    -- Step 1: Make all trainings public for owner
    UPDATE training
    SET owner_id = NULL
    WHERE owner_id = owner_id_to_clear;

    -- Step 2: Make all exercises public for owner
    UPDATE exercise
    SET owner_id = NULL
    WHERE owner_id = owner_id_to_clear;
END;
$$