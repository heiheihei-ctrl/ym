SET NAMES utf8mb4;

-- 查询码：关联产品 + 创建时间/创建人
ALTER TABLE verification
  ADD COLUMN product_type VARCHAR(32) DEFAULT NULL COMMENT '产品编码 yangmei/rice' AFTER code_url,
  ADD COLUMN created_at DATETIME DEFAULT NULL COMMENT '创建时间' AFTER bound_at,
  ADD COLUMN create_by VARCHAR(64) DEFAULT NULL COMMENT '创建人' AFTER created_at;

UPDATE verification SET product_type = 'yangmei' WHERE product_type IS NULL OR product_type = '';
UPDATE verification SET created_at = IFNULL(bound_at, NOW()) WHERE created_at IS NULL;

ALTER TABLE verification ADD INDEX idx_verification_product (product_type);
