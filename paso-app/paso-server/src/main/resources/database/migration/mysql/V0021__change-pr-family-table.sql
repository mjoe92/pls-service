ALTER TABLE pr_number_family
    MODIFY COLUMN name CHAR(3) COMMENT 'The name of the PR-Family',
    MODIFY COLUMN description_de VARCHAR(255) COMMENT 'The english description',
    MODIFY COLUMN description_en VARCHAR(255) COMMENT 'The german description',
    ADD COLUMN id BIGINT NULL;

SET @rownum := 0;
UPDATE pr_number_family
SET id = (@rownum := @rownum + 1)
ORDER BY name;

ALTER TABLE pr_number_family
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (id);

ALTER TABLE pr_number_family MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'The id of the PR-Family';