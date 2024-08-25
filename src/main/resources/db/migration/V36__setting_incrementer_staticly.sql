DO $$
    DECLARE
        roles_counter INT;
        exercise_counter INT;
        training_counter INT;
        training_routine_counter INT;
        training_schedule_counter INT;
    BEGIN
        SELECT (count(*) + 1) INTO roles_counter FROM roles;
        SELECT (count(*) + 1) INTO exercise_counter FROM exercise;
        SELECT (count(*) + 1) INTO training_counter FROM training;
        SELECT (count(*) + 1) INTO training_routine_counter FROM training_routine;
        SELECT (count(*) + 1) INTO training_schedule_counter FROM training_schedule;

        PERFORM setval(pg_get_serial_sequence('roles', 'id'), roles_counter);
        PERFORM setval(pg_get_serial_sequence('exercise', 'id'), exercise_counter);
        PERFORM setval(pg_get_serial_sequence('training', 'id'), training_counter);
        PERFORM setval(pg_get_serial_sequence('training_routine', 'id'), training_routine_counter);
        PERFORM setval(pg_get_serial_sequence('training_schedule', 'id'), training_schedule_counter);
    END $$;


-- set @roles_counter = (select count(*) from roles) + 1;
-- set @exercise_counter = (select count(*) from EXERCISE) + 1;
-- set @training_counter = (select count(*) from TRAINING) + 1;
-- set @training_routine_counter = (select count(*) from TRAINING_ROUTINE) + 1;
-- set @training_schedule_counter = (select count(*) from TRAINING_SCHEDULE) + 1;
--
-- ALTER TABLE ROLES ALTER COLUMN ID RESTART WITH @roles_counter;
-- ALTER TABLE EXERCISE ALTER COLUMN ID RESTART WITH @exercise_counter;
-- ALTER TABLE TRAINING ALTER COLUMN ID RESTART WITH @training_counter;
-- ALTER TABLE TRAINING_ROUTINE ALTER COLUMN ID RESTART WITH @training_routine_counter;
-- ALTER TABLE TRAINING_SCHEDULE ALTER COLUMN ID RESTART WITH @training_schedule_counter;