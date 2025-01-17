/*
 This migration creates locked boolean column in user table.
 This will allow admins to block users which are trolling etc.
 */

ALTER TABLE identity_user
    ADD COLUMN locked BOOL DEFAULT FALSE;