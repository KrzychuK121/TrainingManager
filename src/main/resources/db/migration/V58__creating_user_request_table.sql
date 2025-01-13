CREATE TABLE user_request
(
    id              SERIAL PRIMARY KEY,
    title           VARCHAR(100) NOT NULL,
    description     VARCHAR      NOT NULL,
    request_date    timestamp    NOT NULL,
    requester_id    VARCHAR(255) NOT NULL
        REFERENCES identity_user (id),
    user_closing_id VARCHAR(255)
        REFERENCES identity_user (id)
);