CREATE TABLE IF NOT EXISTS `pst` (
  `pst_id` bigint NOT NULL AUTO_INCREMENT,
  `pst_name` varchar(3) NOT NULL UNIQUE,
  `description_de` varchar(255) NOT NULL,
  `description_en` varchar(255) NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `user_create` varchar(20) NOT NULL,
  `user_change` varchar(20) DEFAULT NULL,
  `timestamp_create` datetime(3) NOT NULL,
  `timestamp_change` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`pst_id`),
  CONSTRAINT `fk_pst_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `pst` (`pst_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


