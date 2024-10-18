-- This code creates procedure that removes training schedule, routines and plans
-- based on training routine id.
CREATE OR REPLACE PROCEDURE delete_training_routine(routine_id_to_delete INTEGER)
    LANGUAGE plpgsql
AS
$$
BEGIN
    -- Step 1: Delete from training_plan by routine_id_to_delete
    DELETE
    FROM training_plan p
    WHERE p.routine_id = routine_id_to_delete;

    -- Step 2: Delete from training_routine
    DELETE
    FROM training_routine r
    WHERE r.id = routine_id_to_delete;

    -- Step 3: Delete any orphaned rows from training_schedule
    DELETE
    FROM training_schedule sch
    WHERE NOT EXISTS (SELECT 1
                      FROM training_plan p
                      WHERE p.schedule_id = sch.id);
END
$$;