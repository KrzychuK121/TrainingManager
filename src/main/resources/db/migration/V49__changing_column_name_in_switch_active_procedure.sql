DROP PROCEDURE switch_active_procedure(integer, character varying);
CREATE PROCEDURE switch_active_procedure(IN routine_id_to_switch INTEGER,
                                         IN provided_owner_id CHARACTER VARYING)
    language plpgsql
as
$$
BEGIN
    -- Step 1: Clear previous active status
    UPDATE training_routine r
    SET active = false
    WHERE r.active = true
      AND r.owner_id = provided_owner_id;

    -- Step 2: Delete from training_routine
    UPDATE training_routine r
    SET active = true
    WHERE r.id = routine_id_to_switch
      AND r.owner_id = provided_owner_id;
END
$$;

alter procedure switch_active_procedure(integer, varchar) owner to postgres;

