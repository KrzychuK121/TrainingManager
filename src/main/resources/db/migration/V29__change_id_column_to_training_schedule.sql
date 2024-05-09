alter table TRAINING_SCHEDULE
add column ID INTEGER auto_increment;

alter table TRAINING_SCHEDULE
drop primary key;
alter table TRAINING_SCHEDULE
add primary key (id);

alter table TRAINING_ROUTINE_SCHEDULE
add column SCHEDULE_ID INTEGER not null;
alter table TRAINING_ROUTINE_SCHEDULE
add constraint fk_id_schedule_id
foreign key (SCHEDULE_ID)
references TRAINING_SCHEDULE;