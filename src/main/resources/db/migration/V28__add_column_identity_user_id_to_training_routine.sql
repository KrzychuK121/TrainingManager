/*
    Truncates are used because previously there were seeding migrations
    and they moved after this migration now. So the table can contain rows in the table.
    Truncate make sure that tables are clean before changing table structure and after that
    they are seeded again.
*/
truncate table TRAINING_ROUTINE_SCHEDULE;

alter table TRAINING_ROUTINE_SCHEDULE
drop constraint FK_ID_ROUTINE_ID;
truncate table TRAINING_ROUTINE;
alter table TRAINING_ROUTINE_SCHEDULE
add constraint FK_ID_ROUTINE_ID
foreign key (ROUTINE_ID)
references TRAINING_ROUTINE(ID);


alter table TRAINING_ROUTINE_SCHEDULE
drop constraint fk_training_id_weekday_training_id_weekday;
truncate table TRAINING_SCHEDULE;

alter table TRAINING_ROUTINE
add column IDENTITY_USER_ID character varying(255) not null;

alter table TRAINING_ROUTINE
add constraint fk_id_identity_user_id
foreign key (IDENTITY_USER_ID)
references IDENTITY_USER(id);