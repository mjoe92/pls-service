ALTER TABLE zo_pr_number_pkz ALTER COLUMN pkz SET DATA TYPE VARCHAR(6);

ALTER TABLE zo_pr_number_pkz ALTER COLUMN einsatz SET DATA TYPE DATE;
ALTER TABLE zo_pr_number_pkz ALTER COLUMN einsatzschl SET DATA TYPE CHAR(11);
ALTER TABLE zo_pr_number_pkz ALTER COLUMN entfall SET DATA TYPE DATE;
ALTER TABLE zo_pr_number_pkz ALTER COLUMN entfallschl SET DATA TYPE CHAR(11);
ALTER TABLE zo_pr_number_pkz ALTER COLUMN description SET DATA TYPE VARCHAR(255);
ALTER TABLE zo_pr_number_pkz ALTER COLUMN additional_description SET DATA TYPE VARCHAR(255);
ALTER TABLE zo_pr_number_pkz ALTER COLUMN status SET DATA TYPE CHAR(3);

ALTER TABLE zo_pr_number_pkz ADD COLUMN pr_number_id BIGINT;

DELETE FROM zo_pr_number_pkz assignment
WHERE NOT EXISTS (
    SELECT 1
    FROM pr_number pr
    WHERE pr.name = assignment.pr_number
);

UPDATE zo_pr_number_pkz assignment
SET pr_number_id = (
    SELECT pr.id
    FROM pr_number pr
    WHERE pr.name = assignment.pr_number
);

ALTER TABLE zo_pr_number_pkz
    ADD CONSTRAINT fk_pr_number_assignment_pkz
    FOREIGN KEY (pkz) REFERENCES product (product_key);

ALTER TABLE zo_pr_number_pkz
    ADD CONSTRAINT fk_pr_number_assignment
    FOREIGN KEY (pr_number_id) REFERENCES pr_number (id);

ALTER TABLE zo_pr_number_pkz DROP COLUMN pr_number;
ALTER TABLE zo_pr_number_pkz DROP COLUMN additional_description;
ALTER TABLE zo_pr_number_pkz DROP COLUMN family;