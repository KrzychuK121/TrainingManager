set @roles_counter = (select count(*) from roles) + 1;
set @exercise_counter = (select count(*) from EXERCISE) + 1;
set @training_counter = (select count(*) from TRAINING) + 1;
set @training_routine_counter = (select count(*) from TRAINING_ROUTINE) + 1;
set @training_schedule_counter = (select count(*) from TRAINING_SCHEDULE) + 1;

ALTER TABLE ROLES ALTER COLUMN ID RESTART WITH @roles_counter;
ALTER TABLE EXERCISE ALTER COLUMN ID RESTART WITH @exercise_counter;
ALTER TABLE TRAINING ALTER COLUMN ID RESTART WITH @training_counter;
ALTER TABLE TRAINING_ROUTINE ALTER COLUMN ID RESTART WITH @training_routine_counter;
ALTER TABLE TRAINING_SCHEDULE ALTER COLUMN ID RESTART WITH @training_schedule_counter;