create table USERS_ROLES
(
    USER_ID CHARACTER VARYING(255) not null,
    ROLE_ID INTEGER                not null,
    primary key (USER_ID, ROLE_ID),
    constraint FK1A530SP0FRIP3XT5H5CRTBL90
        foreign key (USER_ID) references IDENTITY_USER,
    constraint FKJ6M8FWV7OQV74FCEHIR1A9FFY
        foreign key (ROLE_ID) references ROLES
);