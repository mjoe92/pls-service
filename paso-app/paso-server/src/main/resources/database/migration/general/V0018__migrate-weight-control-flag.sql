ALTER TABLE efs_element ADD COLUMN weight_control_flag_new ENUM('YES', 'NO', 'TEMP') NULL default NULL;

update efs_element
set weight_control_flag_new = case weight_control_flag
    when 'J' then 'YES'
    when 'N' then 'NO'
    when 'V' then 'TEMP'
    else null
end;

ALTER TABLE efs_element DROP COLUMN weight_control_flag;
ALTER TABLE efs_element RENAME COLUMN weight_control_flag_new TO weight_control_flag;

ALTER TABLE efs_element_history ADD COLUMN weight_control_flag_new ENUM('YES', 'NO', 'TEMP') NULL default NULL;

update efs_element_history
set weight_control_flag_new = case weight_control_flag
    when 'J' then 'YES'
    when 'N' then 'NO'
    when 'V' then 'TEMP'
    else null
end;

ALTER TABLE efs_element_history DROP COLUMN weight_control_flag;
ALTER TABLE efs_element_history RENAME COLUMN weight_control_flag_new TO weight_control_flag;

ALTER TABLE filtered_out_efs_element ADD COLUMN weight_control_flag_new ENUM('YES', 'NO', 'TEMP') NULL default NULL;

update filtered_out_efs_element
set weight_control_flag_new = case weight_control_flag
    when 'J' then 'YES'
    when 'N' then 'NO'
    when 'V' then 'TEMP'
    else null
end;

ALTER TABLE filtered_out_efs_element DROP COLUMN weight_control_flag;
ALTER TABLE filtered_out_efs_element RENAME COLUMN weight_control_flag_new TO weight_control_flag;
