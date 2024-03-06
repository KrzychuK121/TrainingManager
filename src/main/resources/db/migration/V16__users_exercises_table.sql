create table USERS_EXERCISES (
    USER_ID CHARACTER VARYING(255) not null,
    EXERCISE_ID INTEGER not null,
    primary key (USER_ID,  EXERCISE_ID),
    constraint FK_USERS_EXERCISES
        foreign key (USER_ID) references IDENTITY_USER,
    constraint FK_EXERCISES_USERS
        foreign key (EXERCISE_ID) references EXERCISE
)