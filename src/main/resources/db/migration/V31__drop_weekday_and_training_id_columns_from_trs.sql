alter table TRAINING_PLAN
drop constraint training_routine_schedule_routine_id_weekday_key;
alter table TRAINING_PLAN
drop constraint training_routine_schedule_pkey;
alter table TRAINING_PLAN
add primary key (ROUTINE_ID, SCHEDULE_ID);

alter table TRAINING_PLAN
drop column WEEKDAY;
alter table TRAINING_PLAN
drop column TRAINING_ID;