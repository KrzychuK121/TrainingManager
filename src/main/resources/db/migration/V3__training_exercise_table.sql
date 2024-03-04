create table TRAINING_EXERCISE
(
    TRAINING_ID INTEGER not null,
    EXERCISE_ID INTEGER not null,
    constraint FKEXG36ANBXKJJ0YQSJ81U3X1F4
        foreign key (TRAINING_ID) references TRAINING,
    constraint FKG35G3WKHT1BHQXYAN2CFRKVVA
        foreign key (EXERCISE_ID) references EXERCISE
);