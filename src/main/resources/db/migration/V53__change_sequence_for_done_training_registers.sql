DO
$$
    BEGIN
        PERFORM setval(
            pg_get_serial_sequence('done_training_register', 'id'),
            (SELECT MAX(dtr.id) FROM done_training_register dtr)
                );
    END;
$$;
DO
$$
    BEGIN
        PERFORM setval(
            pg_get_serial_sequence('done_exercise_register', 'id'),
            (SELECT MAX(der.id) FROM done_exercise_register der)
                );
    END;
$$;