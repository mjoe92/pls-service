ALTER TABLE pr_number ALTER COLUMN name SET DATA TYPE CHAR(3);

ALTER TABLE pr_number ALTER COLUMN description_de SET DATA TYPE VARCHAR(255);
ALTER TABLE pr_number ALTER COLUMN description_en SET DATA TYPE VARCHAR(255);

ALTER TABLE pr_number ADD COLUMN pr_family_id BIGINT;

UPDATE pr_number pn
SET pr_family_id = (
    SELECT pf.id
    FROM pr_number_family pf
    WHERE pf.name = pn.family
)
WHERE EXISTS (
    SELECT 1
    FROM pr_number_family pf
    WHERE pf.name = pn.family
);

ALTER TABLE pr_number
    ADD CONSTRAINT fk_pr_number_family
    FOREIGN KEY (pr_family_id)
    REFERENCES pr_number_family (id);

ALTER TABLE pr_number DROP COLUMN family;
ALTER TABLE pr_number DROP COLUMN additional_description;
ALTER TABLE pr_number DROP COLUMN status;
ALTER TABLE pr_number DROP COLUMN einsatz;
ALTER TABLE pr_number DROP COLUMN einsatzschl;
ALTER TABLE pr_number DROP COLUMN entfall;
ALTER TABLE pr_number DROP COLUMN entfallschl;