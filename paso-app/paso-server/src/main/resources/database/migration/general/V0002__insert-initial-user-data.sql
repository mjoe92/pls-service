INSERT INTO `role` VALUES
  (1,'Weight Manager - View','View data only. No editing is allowed'),
  (2,'Weight Manager - Edit','This role allows viewing and editing of data.'),
  (3,'Admin','This role enables some admin functions like MBT import and showing user list.')
;

INSERT INTO `role_permission_mapping` VALUES
  (1,'PART_LIST_READ'),
  (1,'PART_LIST_EXPORT'),
  (1,'CORE_DATA_READ'),
  (1,'SHOW_QUEUE'),
  (2,'PART_LIST_READ'),
  (2,'PART_LIST_WRITE'),
  (2,'PART_LIST_EXPORT'),
  (2,'PART_LIST_COMPARE'),
  (2,'SHOW_HISTORY'),
  (2,'SHOW_QUEUE'),
  (2,'CORE_DATA_READ'),
  (2,'CORE_DATA_WRITE'),
  (3,'USER_MANAGEMENT'),
  (3,'MBT_IMPORT'),
  (3,'SYSTEM_MESSAGE'),
  (3,'PART_LIST_READ'),
  (3,'PART_LIST_WRITE'),
  (3,'PART_LIST_EXPORT'),
  (3,'PART_LIST_COMPARE'),
  (3,'SHOW_HISTORY'),
  (3,'SHOW_QUEUE'),
  (3,'CORE_DATA_READ'),
  (3,'CORE_DATA_WRITE'),
  (3,'PUBLIC_LAYOUT')
;

INSERT INTO `paso_user` VALUES
  ('EOSTESI','2015-01-16 09:56:09','2015-01-16 09:56:09',NULL,'EOSTESI','Simon','Osterloh',NULL,1),
  ('EVINKJO','2023-10-04 12:05:44','2015-01-16 09:56:09',NULL,'EOSTESI','Joost','Vink','Joost.Vink@volkswagen.de',1)
;

INSERT INTO `user_role_mapping` VALUES
  ('EVINKJO',3),
  ('EOSTESI',3)
;
