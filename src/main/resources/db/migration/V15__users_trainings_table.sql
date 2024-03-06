create table USERS_TRAININGS (
     USER_ID CHARACTER VARYING(255) not null,
     TRAINING_ID INTEGER not null,
     primary key (USER_ID, TRAINING_ID),
     constraint FK_USERS
         foreign key (USER_ID) references IDENTITY_USER,
     constraint FK_TRAININGS
         foreign key (TRAINING_ID) references TRAINING
)