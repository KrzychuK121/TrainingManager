create table TRAINING_ROUTINE_SCHEDULE(
    ROUTINE_ID INTEGER,
    TRAINING_ID INTEGER,
    WEEKDAY VARCHAR(12),
    primary key (ROUTINE_ID, TRAINING_ID, WEEKDAY),
    constraint fk_id_routine_id
        foreign key (ROUTINE_ID) references TRAINING_ROUTINE,
    constraint fk_training_id_weekday_training_id_weekday
        foreign key(TRAINING_ID, WEEKDAY) references TRAINING_SCHEDULE,
    UNIQUE (ROUTINE_ID, WEEKDAY)
);