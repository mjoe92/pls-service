CREATE TABLE `admin_activity_log` (
  `user_id` varchar(20) NOT NULL,
  `log_date` datetime NOT NULL,
  `log_text` longtext NOT NULL,
  `log_data` blob
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `cost_group` (
  `cost_group` char(4) NOT NULL,
  `description` varchar(4000) NOT NULL,
  `parent` char(4) DEFAULT NULL,
  `version` bigint NOT NULL,
  UNIQUE KEY `uk_cost_group_version` (`cost_group`,`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `vehicle_part_list` (
  `vehicle_part_list_id` bigint NOT NULL AUTO_INCREMENT,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `weight` decimal(15,3) NOT NULL DEFAULT '0.000',
  `product_key_vehicle` char(4) NOT NULL,
  `product_key_gearbox` char(4) DEFAULT NULL,
  `product_key_motor` char(4) DEFAULT NULL,
  `revision` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`vehicle_part_list_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `efs_element_mara` (
  `efs_element_mara_id` bigint NOT NULL,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `description1_de` varchar(60) NOT NULL,
  `description1_en` varchar(60) DEFAULT NULL,
  `description2_de` varchar(60) DEFAULT NULL,
  `description2_en` varchar(60) DEFAULT NULL,
  `weight_calculated_te` decimal(10,3) NOT NULL DEFAULT '0.000',
  `weight_calculated_te_date` date DEFAULT NULL,
  `weight_estimated_te` decimal(10,3) NOT NULL DEFAULT '0.000',
  `weight_estimated_te_date` date DEFAULT NULL,
  `weight_weighted_te` decimal(10,3) NOT NULL DEFAULT '0.000',
  `weight_weighted_te_date` date DEFAULT NULL,
  `weight_weighted_prod` decimal(10,3) NOT NULL DEFAULT '0.000',
  `weight_weighted_prod_date` date DEFAULT NULL,
  `assembly_indicator` varchar(1) DEFAULT NULL,
  `revision` bigint NOT NULL,
  `part_number` varchar(12) NOT NULL,
  `part_number_vornummer` varchar(3) DEFAULT NULL,
  `part_number_mittelgruppe` varchar(3) DEFAULT NULL,
  `part_number_end_number` varchar(3) DEFAULT NULL,
  `part_number_index` varchar(2) DEFAULT NULL,
  `drawing_date` date DEFAULT NULL,
  `drawing_status` char(2) DEFAULT NULL,
  `constructions_state` varchar(6) DEFAULT NULL,
  `quality` varchar(40) DEFAULT NULL,
  `material_thickness` decimal(7,3) DEFAULT NULL,
  `see_drawing` varchar(12) DEFAULT NULL,
  `responsible_constr_1` varchar(1) DEFAULT NULL,
  `responsible_constr_2` varchar(1) DEFAULT NULL,
  `build_sample_approval` varchar(1) DEFAULT NULL,
  `technically_okay` varchar(3) DEFAULT NULL,
  `release_date_soll` date DEFAULT NULL,
  `designer_name` varchar(20) DEFAULT NULL,
  `designer_cost_group` varchar(5) DEFAULT NULL,
  `designer_phone_number` varchar(15) DEFAULT NULL,
  `k_stand_release_date` date DEFAULT NULL,
  `tio_frei_release_date` date DEFAULT NULL,
  `build_sample_approval_target_date` date DEFAULT NULL,
  `mfp_status` varchar(4) DEFAULT NULL,
  `mfp_thickness` decimal(7,3) DEFAULT NULL,
  `kse_kz` varchar(1) DEFAULT NULL,
  `weight_accepted_from_epis` varchar(1) DEFAULT NULL,
  `vehicle_part_list_id` bigint NOT NULL,
  PRIMARY KEY (`efs_element_mara_id`),
  KEY `ix_efsm_vehicle_part_list_id` (`vehicle_part_list_id`),
  CONSTRAINT `fk_efs_element_mara_vehicle_part_list` FOREIGN KEY (`vehicle_part_list_id`) REFERENCES `vehicle_part_list` (`vehicle_part_list_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `efs_element` (
  `efs_element_id` bigint NOT NULL,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `bom_number` int DEFAULT NULL,
  `product` varchar(4) DEFAULT NULL,
  `aggregate` varchar(4) DEFAULT NULL,
  `deleted` int NOT NULL DEFAULT '0',
  `begin_date` date DEFAULT NULL,
  `begin_date_key` varchar(11) DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `end_date_key` varchar(11) DEFAULT NULL,
  `part_type` varchar(1) DEFAULT NULL,
  `weight_control_flag` char(1) DEFAULT NULL,
  `constructions_group` char(1) DEFAULT NULL,
  `product_structure` varchar(3) DEFAULT NULL,
  `cost_group` varchar(4) DEFAULT NULL,
  `position_variant` varchar(8) DEFAULT NULL,
  `deletion_flag` varchar(1) DEFAULT NULL,
  `quantity` int NOT NULL,
  `quantity_unit` char(3) NOT NULL,
  `quantity_unit_extended` char(1) DEFAULT NULL,
  `node_id` varchar(255) DEFAULT NULL,
  `node_label` varchar(255) DEFAULT NULL,
  `node_level` int DEFAULT NULL,
  `node_type` varchar(8) DEFAULT NULL,
  `node_value_parent` varchar(40) DEFAULT NULL,
  `node_value` varchar(40) DEFAULT NULL,
  `pr_number_rule` varchar(200) DEFAULT NULL,
  `revision` bigint NOT NULL,
  `gap_flag` int NOT NULL,
  `ti_wh_import_id` bigint DEFAULT NULL,
  `tis_sort` bigint DEFAULT NULL,
  `efs_element_mara_id` bigint NOT NULL,
  `vehicle_part_list_id` bigint NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `ap` varchar(10) DEFAULT NULL,
  `set_key` char(3) DEFAULT NULL,
  `duplicate_id` varchar(50) DEFAULT NULL,
  `wahlweise_fall` varchar(10) DEFAULT NULL,
  `wahlweise_nr` int DEFAULT NULL,
  `work_package_number` varchar(6) DEFAULT NULL,
  `process_status` varchar(1) DEFAULT NULL,
  `dmu_relevant` varchar(2) DEFAULT NULL,
  `baukasten_flag` int NOT NULL DEFAULT '0',
  `baukasten_status` varchar(1) DEFAULT NULL,
  `baukasten_node_id` varchar(32) DEFAULT NULL,
  `material_type` varchar(4) DEFAULT NULL,
  `earliest_pvs` date DEFAULT NULL,
  `earliest_ns` date DEFAULT NULL,
  `earliest_sop` date DEFAULT NULL,
  `p_activation_date` date DEFAULT NULL,
  `konstructure_date` date DEFAULT NULL,
  `avon_status` varchar(4) DEFAULT NULL,
  `aggregate_import_date` date DEFAULT NULL,
  `aggregate_created` int DEFAULT NULL,
  `product_data_id` varchar(24) DEFAULT NULL,
  `cog_x` decimal(10,3) DEFAULT NULL,
  `cog_y` decimal(10,3) DEFAULT NULL,
  `cog_z` decimal(10,3) DEFAULT NULL,
  PRIMARY KEY (`efs_element_id`),
  KEY `ix_efs_efs_element_mara_id` (`efs_element_mara_id`),
  KEY `ix_efs_vehicle_part_list_id` (`vehicle_part_list_id`),
  KEY `ix_efs_parent_id` (`parent_id`),
  CONSTRAINT `fk_efs_element_efs_element_mara` FOREIGN KEY (`efs_element_mara_id`) REFERENCES `efs_element_mara` (`efs_element_mara_id`),
  CONSTRAINT `fk_efs_element_efs_element_parent` FOREIGN KEY (`parent_id`) REFERENCES `efs_element` (`efs_element_id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `fk_efs_element_vehicle_part_list` FOREIGN KEY (`vehicle_part_list_id`) REFERENCES `vehicle_part_list` (`vehicle_part_list_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `efs_element_aggregate` (
  `efs_element_id` bigint NOT NULL,
  `product_data_id` varchar(50) NOT NULL,
  `aggregate_import_date` date DEFAULT NULL,
  `aggregate_pls_file_lock_id` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `efs_element_history` (
  `efs_element_history_id` bigint NOT NULL AUTO_INCREMENT,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `bom_number` int DEFAULT NULL,
  `product` varchar(4) DEFAULT NULL,
  `aggregate` varchar(4) DEFAULT NULL,
  `deleted` int NOT NULL DEFAULT '0',
  `begin_date` date DEFAULT NULL,
  `begin_date_key` varchar(11) DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `end_date_key` varchar(11) DEFAULT NULL,
  `part_type` varchar(1) DEFAULT NULL,
  `weight_control_flag` char(1) DEFAULT NULL,
  `constructions_group` char(1) DEFAULT NULL,
  `product_structure` varchar(3) DEFAULT NULL,
  `cost_group` varchar(4) DEFAULT NULL,
  `position_variant` varchar(8) DEFAULT NULL,
  `deletion_flag` varchar(1) DEFAULT NULL,
  `quantity` int NOT NULL,
  `quantity_unit` char(3) NOT NULL,
  `quantity_unit_extended` char(1) DEFAULT NULL,
  `node_id` varchar(255) DEFAULT NULL,
  `node_label` varchar(255) DEFAULT NULL,
  `node_level` int DEFAULT NULL,
  `node_type` varchar(8) DEFAULT NULL,
  `node_value_parent` varchar(40) DEFAULT NULL,
  `node_value` varchar(40) DEFAULT NULL,
  `pr_number_rule` varchar(200) DEFAULT NULL,
  `revision` bigint NOT NULL,
  `gap_flag` int NOT NULL,
  `ti_wh_import_id` bigint DEFAULT NULL,
  `tis_sort` bigint DEFAULT NULL,
  `efs_element_mara_id` bigint NOT NULL,
  `efs_element_id` bigint NOT NULL,
  `vehicle_part_list_id` bigint NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `ap` varchar(10) NOT NULL DEFAULT 'HUT',
  `set_key` char(3) DEFAULT NULL,
  `wahlweise_fall` varchar(10) DEFAULT NULL,
  `wahlweise_nr` int DEFAULT NULL,
  `work_package_number` varchar(6) DEFAULT NULL,
  `process_status` varchar(1) DEFAULT NULL,
  `dmu_relevant` varchar(2) DEFAULT NULL,
  `baukasten_flag` int NOT NULL DEFAULT '0',
  `baukasten_status` varchar(1) DEFAULT NULL,
  `baukasten_node_id` varchar(32) DEFAULT NULL,
  `material_type` varchar(4) DEFAULT NULL,
  `earliest_pvs` date DEFAULT NULL,
  `earliest_ns` date DEFAULT NULL,
  `earliest_sop` date DEFAULT NULL,
  `p_activation_date` date DEFAULT NULL,
  `konstructure_date` date DEFAULT NULL,
  `avon_status` varchar(4) DEFAULT NULL,
  `aggregate_import_date` date DEFAULT NULL,
  `aggregate_created` int DEFAULT NULL,
  `product_data_id` varchar(24) DEFAULT NULL,
  `cog_x` decimal(10,3) DEFAULT NULL,
  `cog_y` decimal(10,3) DEFAULT NULL,
  `cog_z` decimal(10,3) DEFAULT NULL,
  PRIMARY KEY (`efs_element_history_id`),
  KEY `ix_efsh_efs_element_mara_id` (`efs_element_mara_id`),
  KEY `ix_efsh_efs_element_id` (`efs_element_id`),
  KEY `ix_efsh_vehicle_part_list_id` (`vehicle_part_list_id`),
  KEY `ix_efsh_parent_id` (`parent_id`),
  CONSTRAINT `fk_efs_element_history_efs_element` FOREIGN KEY (`efs_element_id`) REFERENCES `efs_element` (`efs_element_id`),
  CONSTRAINT `fk_efs_element_history_efs_element_mara` FOREIGN KEY (`efs_element_mara_id`) REFERENCES `efs_element_mara` (`efs_element_mara_id`),
  CONSTRAINT `fk_efs_element_history_efs_element_parent` FOREIGN KEY (`parent_id`) REFERENCES `efs_element` (`efs_element_id`),
  CONSTRAINT `fk_efs_element_history_vehicle_part_list` FOREIGN KEY (`vehicle_part_list_id`) REFERENCES `vehicle_part_list` (`vehicle_part_list_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `efs_element_mara_history` (
  `efs_element_mara_history_id` bigint NOT NULL AUTO_INCREMENT,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `description1_de` varchar(60) NOT NULL,
  `description1_en` varchar(60) DEFAULT NULL,
  `description2_de` varchar(60) DEFAULT NULL,
  `description2_en` varchar(60) DEFAULT NULL,
  `weight_calculated_te` decimal(10,3) NOT NULL DEFAULT '0.000',
  `weight_calculated_te_date` date DEFAULT NULL,
  `weight_estimated_te` decimal(10,3) NOT NULL DEFAULT '0.000',
  `weight_estimated_te_date` date DEFAULT NULL,
  `weight_weighted_te` decimal(10,3) NOT NULL DEFAULT '0.000',
  `weight_weighted_te_date` date DEFAULT NULL,
  `weight_weighted_prod` decimal(10,3) NOT NULL DEFAULT '0.000',
  `weight_weighted_prod_date` date DEFAULT NULL,
  `assembly_indicator` varchar(1) DEFAULT NULL,
  `revision` bigint NOT NULL,
  `part_number` varchar(12) NOT NULL,
  `part_number_vornummer` varchar(3) DEFAULT NULL,
  `part_number_mittelgruppe` varchar(3) DEFAULT NULL,
  `part_number_end_number` varchar(3) DEFAULT NULL,
  `part_number_index` varchar(2) DEFAULT NULL,
  `drawing_date` date DEFAULT NULL,
  `drawing_status` char(2) DEFAULT NULL,
  `constructions_state` varchar(6) DEFAULT NULL,
  `quality` varchar(40) DEFAULT NULL,
  `material_thickness` decimal(7,3) DEFAULT NULL,
  `see_drawing` varchar(12) DEFAULT NULL,
  `responsible_constr_1` varchar(1) DEFAULT NULL,
  `responsible_constr_2` varchar(1) DEFAULT NULL,
  `build_sample_approval` varchar(1) DEFAULT NULL,
  `technically_okay` varchar(3) DEFAULT NULL,
  `release_date_soll` date DEFAULT NULL,
  `designer_name` varchar(20) DEFAULT NULL,
  `designer_cost_group` varchar(5) DEFAULT NULL,
  `designer_phone_number` varchar(15) DEFAULT NULL,
  `k_stand_release_date` date DEFAULT NULL,
  `tio_frei_release_date` date DEFAULT NULL,
  `build_sample_approval_target_date` date DEFAULT NULL,
  `mfp_status` varchar(4) DEFAULT NULL,
  `mfp_thickness` decimal(7,3) DEFAULT NULL,
  `kse_kz` varchar(1) DEFAULT NULL,
  `weight_accepted_from_epis` varchar(1) DEFAULT NULL,
  `efs_element_mara_id` bigint NOT NULL,
  `vehicle_part_list_id` bigint NOT NULL,
  PRIMARY KEY (`efs_element_mara_history_id`),
  KEY `ix_efs_element_mara_id` (`efs_element_mara_id`),
  KEY `ix_vehicle_part_list_id` (`vehicle_part_list_id`),
  CONSTRAINT `fk_efs_element_mara_history_efs_element_mara` FOREIGN KEY (`efs_element_mara_id`) REFERENCES `efs_element_mara` (`efs_element_mara_id`),
  CONSTRAINT `fk_efs_element_mara_history_vehicle_part_list` FOREIGN KEY (`vehicle_part_list_id`) REFERENCES `vehicle_part_list` (`vehicle_part_list_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `efs_inspector_ignore` (
  `EFS_ELEMENT_ID` bigint NOT NULL,
  `INSPECTOR_ENTRY_TYPE` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `filtered_out_efs_element` (
  `filtered_out_efs_element_id` bigint NOT NULL AUTO_INCREMENT,
  `reason` varchar(255) DEFAULT NULL,
  `vehicle_config_id` bigint DEFAULT NULL,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `bom_number` int DEFAULT NULL,
  `product` varchar(4) DEFAULT NULL,
  `aggregate` varchar(4) DEFAULT NULL,
  `deleted` int NOT NULL DEFAULT '0',
  `begin_date` date DEFAULT NULL,
  `begin_date_key` varchar(11) DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `end_date_key` varchar(11) DEFAULT NULL,
  `part_type` varchar(1) DEFAULT NULL,
  `weight_control_flag` char(1) DEFAULT NULL,
  `ap` varchar(10) NOT NULL DEFAULT 'HUT',
  `constructions_group` char(1) DEFAULT NULL,
  `product_structure` varchar(3) DEFAULT NULL,
  `cost_group` varchar(4) DEFAULT NULL,
  `position_variant` varchar(8) DEFAULT NULL,
  `deletion_flag` varchar(1) DEFAULT NULL,
  `quantity` int NOT NULL,
  `quantity_unit` char(3) NOT NULL,
  `quantity_unit_extended` char(1) DEFAULT NULL,
  `node_id` varchar(255) DEFAULT NULL,
  `node_label` varchar(255) DEFAULT NULL,
  `node_level` int DEFAULT NULL,
  `node_type` varchar(8) DEFAULT NULL,
  `node_value_parent` varchar(40) DEFAULT NULL,
  `node_value` varchar(40) DEFAULT NULL,
  `pr_number_rule` varchar(200) DEFAULT NULL,
  `revision` bigint NOT NULL,
  `gap_flag` int NOT NULL,
  `set_key` varchar(3) DEFAULT NULL,
  `duplicate_id` varchar(50) DEFAULT NULL,
  `wahlweise_fall` varchar(10) DEFAULT NULL,
  `wahlweise_nr` int DEFAULT NULL,
  `work_package_number` varchar(6) DEFAULT NULL,
  `process_status` varchar(1) DEFAULT NULL,
  `dmu_relevant` varchar(2) DEFAULT NULL,
  `baukasten_flag` int NOT NULL DEFAULT '0',
  `baukasten_status` varchar(1) DEFAULT NULL,
  `baukasten_node_id` varchar(32) DEFAULT NULL,
  `material_type` varchar(4) DEFAULT NULL,
  `earliest_pvs` date DEFAULT NULL,
  `earliest_ns` date DEFAULT NULL,
  `earliest_sop` date DEFAULT NULL,
  `p_activation_date` date DEFAULT NULL,
  `konstructure_date` date DEFAULT NULL,
  `avon_status` varchar(4) DEFAULT NULL,
  `ti_wh_import_id` bigint DEFAULT NULL,
  `tis_sort` bigint DEFAULT NULL,
  `efs_element_mara_id` bigint NOT NULL,
  `cog_x` decimal(10,3) DEFAULT NULL,
  `cog_y` decimal(10,3) DEFAULT NULL,
  `cog_z` decimal(10,3) DEFAULT NULL,
  PRIMARY KEY (`filtered_out_efs_element_id`),
  KEY `ix_fo_efs_element_mara_id` (`efs_element_mara_id`),
  CONSTRAINT `fk_filtered_out_efs_element_efs_element_mara` FOREIGN KEY (`efs_element_mara_id`) REFERENCES `efs_element_mara` (`efs_element_mara_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `resource` (
  `resource_id` bigint NOT NULL AUTO_INCREMENT,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  PRIMARY KEY (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `message` (
  `message_id` bigint NOT NULL AUTO_INCREMENT,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `message_betreff` varchar(100) NOT NULL,
  `message` varchar(4000) NOT NULL,
  `sender` varchar(100) NOT NULL,
  `message_type` varchar(20) NOT NULL,
  `resource_id` bigint NOT NULL,
  PRIMARY KEY (`message_id`),
  KEY `ix_resource_id` (`resource_id`),
  CONSTRAINT `fk_message_resource` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `sales_region` (
  `sales_region_id` char(3) NOT NULL,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `description_de` varchar(255) NOT NULL,
  `description_en` varchar(255) NOT NULL,
  `relevant` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`sales_region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `model_import` (
  `model_import_id` bigint NOT NULL AUTO_INCREMENT,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `model_year` int NOT NULL,
  `import_status` varchar(11) NOT NULL,
  `sales_key` char(2) NOT NULL,
  `sales_region_id` char(3) NOT NULL,
  PRIMARY KEY (`model_import_id`),
  KEY `ix_sales_region_id` (`sales_region_id`),
  CONSTRAINT `fk_model_import_sales_region` FOREIGN KEY (`sales_region_id`) REFERENCES `sales_region` (`sales_region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `model` (
  `model_id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `begin_date` date NOT NULL,
  `end_date` date NOT NULL,
  `model_version` varchar(2) NOT NULL,
  `model_key` varchar(6) NOT NULL,
  `status` varchar(10) NOT NULL,
  `model_import_id` bigint NOT NULL,
  PRIMARY KEY (`model_id`),
  KEY `ix_model_import_id` (`model_import_id`),
  CONSTRAINT `fk_model_model_import` FOREIGN KEY (`model_import_id`) REFERENCES `model_import` (`model_import_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `part_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` int NOT NULL,
  `mgr_start` int DEFAULT NULL,
  `mgr_end` int DEFAULT NULL,
  `ugr` int DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `part_list_view_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `rule_description` varchar(150) NOT NULL,
  `cost_group` varchar(100) DEFAULT NULL,
  `part_groups` varchar(255) DEFAULT NULL,
  `part_list_view_mode` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `pr_number` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` char(3) NOT NULL,
  `family` char(5) NOT NULL,
  `description_de` varchar(255) DEFAULT NULL,
  `description_en` varchar(255) DEFAULT NULL,
  `additional_description` varchar(250) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `einsatz` date DEFAULT NULL,
  `einsatzschl` varchar(11) DEFAULT NULL,
  `entfall` date DEFAULT NULL,
  `entfallschl` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ix_pr_number_family` (`family`),
  KEY `ix_pr_number_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `pr_number_family` (
  `name` char(3) NOT NULL,
  `description_de` varchar(255) DEFAULT NULL,
  `description_en` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `set_version` (
  `set_version_id` bigint NOT NULL AUTO_INCREMENT,
  `set_version_name` varchar(255) NOT NULL,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime NOT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) NOT NULL,
  PRIMARY KEY (`set_version_id`),
  UNIQUE KEY `set_version_name` (`set_version_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `set_key` (
  `set_key` char(3) NOT NULL,
  `description` varchar(255) NOT NULL,
  `set_version_id` bigint NOT NULL DEFAULT '1',
  `parent` char(3) DEFAULT NULL,
  PRIMARY KEY (`set_key`,`set_version_id`),
  KEY `ix_sk_set_version_id` (`set_version_id`),
  CONSTRAINT `fk_sk_version` FOREIGN KEY (`set_version_id`) REFERENCES `set_version` (`set_version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `product` (
  `product_key` varchar(5) NOT NULL,
  `set_version_id` bigint NOT NULL DEFAULT '1',
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime NOT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) NOT NULL,
  PRIMARY KEY (`product_key`),
  UNIQUE KEY `product_key` (`product_key`),
  KEY `ix_p_set_version_id` (`set_version_id`),
  CONSTRAINT `fk_p_set_version_id` FOREIGN KEY (`set_version_id`) REFERENCES `set_version` (`set_version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `role_permission_mapping` (
  `fk_role` bigint NOT NULL,
  `permission` varchar(255) NOT NULL,
  KEY `ix_fk_role` (`fk_role`),
  CONSTRAINT `role_permission_mapping_fk_role` FOREIGN KEY (`fk_role`) REFERENCES `role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `seq_efs_element` (
  `next_val` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `seq_efs_element` VALUES (1);


CREATE TABLE `seq_efs_element_history` (
  `next_val` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `seq_efs_element_history` VALUES (1);


CREATE TABLE `seq_efs_element_mara` (
  `next_val` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `seq_efs_element_mara` VALUES (1);


CREATE TABLE `seq_efs_element_mara_history` (
  `next_val` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO `seq_efs_element_mara_history` VALUES (1);


CREATE TABLE `smart_fix` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `field` varchar(100) NOT NULL,
  `old_value` varchar(100) DEFAULT NULL,
  `new_value` varchar(100) DEFAULT NULL,
  `active` int DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `table_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `user_id` varchar(100) NOT NULL,
  `selected_columns` longtext,
  `selected_column_ids` longtext,
  `is_public` int DEFAULT '0',
  `is_default` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `ti_wh_maktx` (
  `ti_wh_maktx_id` bigint NOT NULL AUTO_INCREMENT,
  `bezeichnung` varchar(40) DEFAULT NULL,
  `sprache` varchar(1) NOT NULL,
  `teilenummer` varchar(18) NOT NULL,
  `ti_wh_import_id` bigint NOT NULL,
  PRIMARY KEY (`ti_wh_maktx_id`),
  UNIQUE KEY `uk_teilenummer_ti_wh_import_id` (`teilenummer`,`ti_wh_import_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `ti_wh_ebom` (
  `ti_wh_ebom_id` bigint NOT NULL AUTO_INCREMENT,
  `einsatz_datum` datetime DEFAULT NULL,
  `entfall_datum` datetime DEFAULT NULL,
  `anzahl` int DEFAULT NULL,
  `node_id` varchar(255) DEFAULT NULL,
  `node_label` varchar(255) DEFAULT NULL,
  `prnr_regel` varchar(200) DEFAULT NULL,
  `sort_` bigint DEFAULT NULL,
  `teilenummer` varchar(18) DEFAULT NULL,
  `ti_wh_import_id` bigint NOT NULL,
  `set_key` char(3) DEFAULT NULL,
  PRIMARY KEY (`ti_wh_ebom_id`),
  KEY `ix_teilenummer_ti_wh_import_id` (`teilenummer`,`ti_wh_import_id`),
  CONSTRAINT `fk_ti_wh_ebom_ti_wh_maktx` FOREIGN KEY (`teilenummer`, `ti_wh_import_id`) REFERENCES `ti_wh_maktx` (`teilenummer`, `ti_wh_import_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `ti_wh_import` (
  `ti_wh_import_id` bigint NOT NULL AUTO_INCREMENT,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `product_key` char(4) NOT NULL,
  `import_status` varchar(11) NOT NULL,
  PRIMARY KEY (`ti_wh_import_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `ti_wh_mara` (
  `ti_wh_mara_id` bigint NOT NULL AUTO_INCREMENT,
  `bezeichnung2` varchar(60) DEFAULT NULL,
  `teilenummer` varchar(18) NOT NULL,
  `ti_wh_import_id` bigint NOT NULL,
  PRIMARY KEY (`ti_wh_mara_id`),
  UNIQUE KEY `uk_mara_teilenummer_ti_wh_import_id` (`teilenummer`,`ti_wh_import_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `paso_user` (
  `user_id` varchar(255) NOT NULL,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT 1,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `user_group` (
  `user_group_id` bigint NOT NULL AUTO_INCREMENT,
  `brand` varchar(50) NOT NULL,
  `user_group_name` varchar(255) NOT NULL,
  `write_access` bit(1) NOT NULL DEFAULT 0,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime NOT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) NOT NULL,
  PRIMARY KEY (`user_group_id`),
  UNIQUE KEY `user_group_name` (`user_group_name`),
  KEY `ix_ug_user_group_name` (`user_group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `user_group_user` (
  `user_group_id` bigint NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`user_group_id`,`user_id`),
  KEY `ix_ugu_user_group_id` (`user_group_id`),
  KEY `ix_ugu_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `user_group_vehicle_config` (
  `user_group_id` bigint NOT NULL,
  `vehicle_config_id` bigint NOT NULL,
  PRIMARY KEY (`user_group_id`,`vehicle_config_id`),
  KEY `ix_ugvc_user_group_id` (`user_group_id`),
  KEY `ix_ugvc_vehicle_config_id` (`vehicle_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `user_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) NOT NULL,
  `message` longtext NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_read` int NOT NULL DEFAULT '0',
  `TYPE` varchar(100) NOT NULL DEFAULT 'ANNOUNCEMENT',
  `vehicle_config_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `user_property` (
  `user_property_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) NOT NULL,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `property_type` varchar(255) NOT NULL,
  `user_data` longtext NOT NULL,
  PRIMARY KEY (`user_property_id`),
  KEY `ix_up_sales_region_id` (`user_id`),
  CONSTRAINT `fk_user_property_user` FOREIGN KEY (`user_id`) REFERENCES `paso_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `user_role_mapping` (
  `user_id` varchar(255) NOT NULL,
  `fk_role` bigint NOT NULL,
  KEY `ix_ur_fk_role` (`fk_role`),
  KEY `ix_ur_user_id` (`user_id`),
  CONSTRAINT `user_role_mapping_fk_role` FOREIGN KEY (`fk_role`) REFERENCES `role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_role_mapping_fk_user` FOREIGN KEY (`user_id`) REFERENCES `paso_user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `vehicle_project` (
  `vehicle_project_id` bigint NOT NULL AUTO_INCREMENT,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `brand_code` char(2) NOT NULL,
  `product_key` varchar(5) NOT NULL,
  `project_name` varchar(255) NOT NULL,
  `sales_key` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `first_model_year` int DEFAULT NULL,
  `platform` varchar(20) NOT NULL,
  `archive` boolean DEFAULT '0',
  PRIMARY KEY (`vehicle_project_id`),
  UNIQUE KEY `uk_project_name` (`project_name`),
  KEY `ix_vp_product_key` (`product_key`),
  CONSTRAINT `fk_vp_product_key` FOREIGN KEY (`product_key`) REFERENCES `product` (`product_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `vehicle_config` (
  `vehicle_config_id` bigint NOT NULL AUTO_INCREMENT,
  `timestamp_change` datetime DEFAULT NULL,
  `timestamp_create` datetime DEFAULT NULL,
  `user_change` varchar(255) DEFAULT NULL,
  `user_create` varchar(255) DEFAULT NULL,
  `description` varchar(4000) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `valid_date` date NOT NULL,
  `model_year` int DEFAULT NULL,
  `pr_number_string` varchar(4000) DEFAULT NULL,
  `vehicle_project_id` bigint NOT NULL,
  `model_id` bigint DEFAULT NULL,
  `model_import_id` bigint DEFAULT NULL,
  `resource_id` bigint NOT NULL,
  `vehicle_ti_wh_import_id` bigint DEFAULT NULL,
  `gearbox_ti_wh_import_id` bigint DEFAULT NULL,
  `motor_ti_wh_import_id` bigint DEFAULT NULL,
  `sales_region_id` char(3) DEFAULT NULL,
  `vehicle_part_list_id` bigint DEFAULT NULL,
  `set_version_id` bigint NOT NULL DEFAULT '1',
  `cost_group_version` bigint NOT NULL,
  `deletion_date` date DEFAULT NULL,
  `pls_product_data_id` varchar(24) DEFAULT NULL,
  `pls_data_id` bigint DEFAULT NULL,
  `pls_data_lock_id` varchar(30) DEFAULT NULL,
  `pls_status` varchar(50) DEFAULT NULL,
  `pls_position` int DEFAULT NULL,
  `smart_fix_active` int DEFAULT '0',
  PRIMARY KEY (`vehicle_config_id`),
  UNIQUE KEY `uk_resource_id` (`resource_id`),
  KEY `ix_vc_vehicle_project_id` (`vehicle_project_id`),
  KEY `ix_vc_model_id` (`model_id`),
  KEY `ix_vc_model_import_id` (`model_import_id`),
  KEY `ix_vc_vehicle_ti_wh_import_id` (`vehicle_ti_wh_import_id`),
  KEY `ix_vc_gearbox_ti_wh_import_id` (`gearbox_ti_wh_import_id`),
  KEY `ix_vc_motor_ti_wh_import_id` (`motor_ti_wh_import_id`),
  KEY `ix_vc_sales_region_id` (`sales_region_id`),
  KEY `ix_vc_set_version_id` (`set_version_id`),
  CONSTRAINT `fk_vc_version` FOREIGN KEY (`set_version_id`) REFERENCES `set_version` (`set_version_id`),
  CONSTRAINT `fk_vehicle_config_model` FOREIGN KEY (`model_id`) REFERENCES `model` (`model_id`),
  CONSTRAINT `fk_vehicle_config_model_import` FOREIGN KEY (`model_import_id`) REFERENCES `model_import` (`model_import_id`),
  CONSTRAINT `fk_vehicle_config_resource` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`resource_id`),
  CONSTRAINT `fk_vehicle_config_sales_region` FOREIGN KEY (`sales_region_id`) REFERENCES `sales_region` (`sales_region_id`),
  CONSTRAINT `fk_vehicle_config_ti_wh_import_gearbox` FOREIGN KEY (`gearbox_ti_wh_import_id`) REFERENCES `ti_wh_import` (`ti_wh_import_id`),
  CONSTRAINT `fk_vehicle_config_ti_wh_import_motor` FOREIGN KEY (`motor_ti_wh_import_id`) REFERENCES `ti_wh_import` (`ti_wh_import_id`),
  CONSTRAINT `fk_vehicle_config_ti_wh_import_vehicle` FOREIGN KEY (`vehicle_ti_wh_import_id`) REFERENCES `ti_wh_import` (`ti_wh_import_id`),
  CONSTRAINT `fk_vehicle_config_vehicle_project` FOREIGN KEY (`vehicle_project_id`) REFERENCES `vehicle_project` (`vehicle_project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `vehicle_config_category_status` (
  `vehicle_config_category` varchar(20) NOT NULL,
  `vehicle_config_id` bigint NOT NULL,
  `vehicle_config_status` varchar(12) NOT NULL,
  PRIMARY KEY (`vehicle_config_id`,`vehicle_config_category`),
  KEY `ix_vehicle_config_id` (`vehicle_config_id`),
  CONSTRAINT `fk_vehicle_config_category_status_vehicle_config` FOREIGN KEY (`vehicle_config_id`) REFERENCES `vehicle_config` (`vehicle_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `zo_pr_number_model` (
  `PR_NUMBER` varchar(3) NOT NULL,
  `FK_MODEL` int NOT NULL,
  `SALES_SETTING` varchar(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `zo_pr_number_pkz` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pkz` char(10) NOT NULL,
  `pr_number` char(3) NOT NULL,
  `einsatz` date DEFAULT NULL,
  `einsatzschl` char(11) DEFAULT NULL,
  `entfall` date DEFAULT NULL,
  `entfallschl` char(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `additional_description` varchar(255) DEFAULT NULL,
  `family` char(5) DEFAULT NULL,
  `status` char(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `zo_pr_number_vehicle_project` (
  `FK_PR_NUMBER` int NOT NULL,
  `FK_VEHICLE_PROJECT` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
