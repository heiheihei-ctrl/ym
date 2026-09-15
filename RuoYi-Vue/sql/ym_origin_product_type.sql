-- 产地按产品类型区分：yangmei 杨梅 / rice 五常大米
ALTER TABLE origins
  ADD COLUMN product_type VARCHAR(32) DEFAULT 'yangmei' COMMENT '产品类型：yangmei杨梅 / rice五常大米' AFTER name;

UPDATE origins SET product_type = 'yangmei' WHERE product_type IS NULL OR product_type = '';
