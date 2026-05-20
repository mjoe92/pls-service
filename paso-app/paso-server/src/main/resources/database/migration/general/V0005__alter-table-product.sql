ALTER TABLE `vehicle_project`
DROP CONSTRAINT fk_vp_product_key;

ALTER TABLE `vehicle_project`
MODIFY `product_key` varchar(6);

ALTER TABLE `product`
MODIFY `product_key` varchar(6) NOT NULL;

ALTER TABLE `vehicle_project`
ADD CONSTRAINT `fk_vp_product_key`
FOREIGN KEY (`product_key`)
REFERENCES `product` (`product_key`);
