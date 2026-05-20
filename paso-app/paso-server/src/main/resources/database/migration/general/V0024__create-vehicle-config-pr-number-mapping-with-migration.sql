-- Mapping setup
CREATE TABLE vehicle_config_pr_number_mapping (
    vehicle_config_id BIGINT NOT NULL,
    pr_assignment_id BIGINT NOT NULL,
    UNIQUE KEY uk_vehicle_config_pr_number_mapping (vehicle_config_id, pr_assignment_id),
    CONSTRAINT fk_mapping_vehicle_config FOREIGN KEY (vehicle_config_id) REFERENCES vehicle_config (vehicle_config_id),
    CONSTRAINT fk_mapping_pr_assignment FOREIGN KEY (pr_assignment_id) REFERENCES zo_pr_number_pkz (id)
);

-- Migration
INSERT INTO vehicle_config_pr_number_mapping (vehicle_config_id, pr_assignment_id)
SELECT config.vehicle_config_id, prAssignment.id
FROM vehicle_config config
JOIN vehicle_project project ON project.vehicle_project_id = config.vehicle_project_id
JOIN pr_number prNumber
    ON config.pr_number_string IS NOT NULL
   AND config.pr_number_string <> ''
   AND CONCAT('+', config.pr_number_string, '+') LIKE CONCAT('%+', prNumber.name, '+%')
JOIN (
    SELECT pkz, pr_number_id, MAX(id) AS max_id
    FROM zo_pr_number_pkz
    GROUP BY pkz, pr_number_id
) latestAssignmentKey
    ON latestAssignmentKey.pkz = project.product_key
   AND latestAssignmentKey.pr_number_id = prNumber.id
JOIN zo_pr_number_pkz prAssignment
    ON prAssignment.id = latestAssignmentKey.max_id;