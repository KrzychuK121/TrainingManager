alter table TRAINING_PLAN
drop constraint CONSTRAINT_FAE;
alter table TRAINING_PLAN
drop primary key;
alter table TRAINING_PLAN
add primary key (ROUTINE_ID, SCHEDULE_ID);

alter table TRAINING_PLAN
drop column WEEKDAY;
alter table TRAINING_PLAN
drop column TRAINING_ID;