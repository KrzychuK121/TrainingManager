ALTER TABLE PUBLIC.EXERCISE ADD BODY_PART VARCHAR(30);
UPDATE PUBLIC.EXERCISE
SET BODY_PART = 'UPPER_ABS'
WHERE ID IN (2, 3);
UPDATE PUBLIC.EXERCISE
SET EXERCISE.BODY_PART = 'CHEST'
WHERE ID IN (1, 4);