ALTER TABLE pr_number
    MODIFY COLUMN name CHAR(3) COMMENT 'The name of the PR-Number',
    MODIFY COLUMN description_de VARCHAR(255) COMMENT 'The german description',
    MODIFY COLUMN description_en VARCHAR(255) COMMENT 'The english description',
    ADD COLUMN pr_family_id BIGINT COMMENT 'The reference id to the PR-Family';

UPDATE pr_number prNumber
JOIN pr_number_family prFamily ON prFamily.name COLLATE utf8mb4_unicode_ci = prNumber.family COLLATE utf8mb4_unicode_ci
SET prNumber.pr_family_id = prFamily.id;

ALTER TABLE pr_number
    ADD CONSTRAINT fk_pr_number_family
    FOREIGN KEY (pr_family_id)
    REFERENCES pr_number_family (id);

ALTER TABLE pr_number
    DROP COLUMN family,
    DROP COLUMN additional_description,
    DROP COLUMN status,
    DROP COLUMN einsatz,
    DROP COLUMN einsatzschl,
    DROP COLUMN entfall,
    DROP COLUMN entfallschl;