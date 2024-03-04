create table EXERCISE
(
    ID          INTEGER auto_increment
        primary key,
    DESCRIPTION CHARACTER VARYING(300),
    NAME        CHARACTER VARYING(100),
    REPETITION  INTEGER not null,
    ROUNDS      INTEGER not null,
    TIME        TIME(6),
    check (("REPETITION" >= 0)
        AND ("REPETITION" <= 100)),
    check (("ROUNDS" >= 1)
        AND ("ROUNDS" <= 10))
);