UPDATE user_role_mapping
SET fk_role = 2 WHERE fk_role = 1;

UPDATE user_role_mapping
SET fk_role = 1 WHERE fk_role = 3;

DELETE FROM role_permission_mapping WHERE fk_role = 1;

UPDATE role_permission_mapping
SET fk_role = 1 WHERE fk_role = 3;

UPDATE role
SET name='Admin', description = 'This role has access to the user administration and has full authority in the system in order to update and maintain the Master Data as well as to be able to intervene in the case of support.'
WHERE id = 1;

UPDATE role
SET name='Weight Manager', description = 'This role enables user to create and check single vehicle part lists in order to analyze, summarize and compare their weights.'
WHERE id = 2;

DELETE FROM role WHERE id = 3;
