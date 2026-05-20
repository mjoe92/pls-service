ALTER TABLE zo_pr_number_pkz
    MODIFY COLUMN pkz VARCHAR(6) COMMENT 'The id of the product',
    MODIFY COLUMN einsatz DATE COMMENT 'The start date',
    MODIFY COLUMN einsatzschl CHAR(11) COMMENT 'The start key',
    MODIFY COLUMN entfall DATE COMMENT 'The end date',
    MODIFY COLUMN entfallschl CHAR(11) COMMENT 'The end key',
    MODIFY COLUMN description VARCHAR(255) COMMENT 'The main description',
    MODIFY COLUMN additional_description VARCHAR(255) COMMENT 'The additional description',
    MODIFY COLUMN status CHAR(3) COMMENT 'The status',
    ADD COLUMN pr_number_id BIGINT COMMENT 'The reference id to the PR-Number';

UPDATE zo_pr_number_pkz assignment
    JOIN pr_number prNumber ON prNumber.name = assignment.pr_number
    SET assignment.pr_number_id = prNumber.id;

ALTER TABLE zo_pr_number_pkz
    ADD CONSTRAINT fk_pr_assignment_pkz FOREIGN KEY (pkz) REFERENCES product (product_key),
    ADD CONSTRAINT fk_pr_number_assignment FOREIGN KEY (pr_number_id) REFERENCES pr_number (id);

ALTER TABLE zo_pr_number_pkz
    DROP COLUMN pr_number,
    DROP COLUMN additional_description,
    DROP COLUMN family;