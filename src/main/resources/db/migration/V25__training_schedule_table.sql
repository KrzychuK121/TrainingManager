create table TRAINING_SCHEDULE(
    TRAINING_ID INTEGER,
    WEEKDAY VARCHAR(12),
    primary key(TRAINING_ID, WEEKDAY),
    constraint fk_id_training_id
        foreign key(TRAINING_ID) references TRAINING
);