ALTER TABLE PUBLIC.IDENTITY_USER
ADD CONSTRAINT usernameCheck check (
    (LENGTH(USERNAME) >= 8) AND (LENGTH(USERNAME) <= 20)
)