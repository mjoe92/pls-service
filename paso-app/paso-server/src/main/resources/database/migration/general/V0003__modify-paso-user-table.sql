ALTER TABLE `paso_user`
ADD COLUMN `cost_center` varchar(10) NOT NULL DEFAULT '0';

ALTER TABLE `paso_user`
ADD COLUMN `cost_center_changed_at` datetime NULL DEFAULT NULL;
