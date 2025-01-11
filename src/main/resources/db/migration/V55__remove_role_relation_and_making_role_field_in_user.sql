/*
 This migration is responsible for removing many-to-many relationship
 between role and user. This relation was actually used like one-to-many
 so it have to be changed like this. This approach also helps decide
 which role user actually presents and which privileges he should have.
 In this case, managing roles by frontend are easier than to decide which role
 is the most valuable and how to show content based on this.
 */

ALTER TABLE identity_user
    ADD COLUMN role VARCHAR(20);

UPDATE identity_user iu
SET role = subquery.new_role_name
FROM (SELECT ur.user_id, SUBSTRING(r.name, 6, LENGTH(r.name)) AS new_role_name
      FROM roles r
               INNER JOIN users_roles ur
                          ON r.id = ur.role_id) subquery
WHERE iu.id = subquery.user_id;

ALTER TABLE identity_user
    ALTER COLUMN role
        SET NOT NULL;

DROP TABLE users_roles, roles;