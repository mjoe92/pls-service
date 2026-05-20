ALTER TABLE product MODIFY product_type VARCHAR(6);
UPDATE product SET product_type = NULL WHERE product_type = '';
