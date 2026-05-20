CREATE TABLE `mbt_import_timestamp` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_name` varchar(100) NOT NULL,
  `file_creation` datetime NOT NULL,
  `import_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `file_name_key` (`file_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `mbt_import_timestamp` VALUES
(1, 'DZU.R11K2H.ZU657A.FAMZIP','0001-01-01 00:00:00', '0001-01-01 00:00:00'),
(2, 'DZU.R11K2H.ZU657A.PKZZIP','0001-01-01 00:00:00', '0001-01-01 00:00:00'),
(3, 'DZU.R11K2H.ZU657A.PRNVZIP','0001-01-01 00:00:00', '0001-01-01 00:00:00'),
(4, 'DZU.R11K2H.ZU657A.PRZZIP','0001-01-01 00:00:00', '0001-01-01 00:00:00');
