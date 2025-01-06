DROP PROCEDURE IF EXISTS switch_active_procedure;
create procedure switch_active_procedure(IN routine_id_to_switch integer,
                                         IN owner_id varchar)
    language plpgsql
as
$$
BEGIN
    -- Step 1: Clear previous active status
    UPDATE training_routine r
    SET active = false
    WHERE r.active = true
      AND r.identity_user_id = owner_id;

    -- Step 2: Delete from training_routine
    UPDATE training_routine r
    SET active = true
    WHERE r.id = routine_id_to_switch
      AND r.identity_user_id = owner_id;
END
$$;