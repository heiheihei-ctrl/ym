-- 五常大米见证书字段：产品类型 + 加工厂家/仓库/收割与加工时间
-- 本地执行；勿随意改远程库

ALTER TABLE certificates
  ADD COLUMN product_type VARCHAR(32) DEFAULT 'yangmei' COMMENT '产品类型：yangmei杨梅 / rice五常大米' AFTER batch_id,
  ADD COLUMN processor VARCHAR(200) DEFAULT NULL COMMENT '加工包装厂家（大米）' AFTER origin,
  ADD COLUMN warehouse VARCHAR(200) DEFAULT NULL COMMENT '仓库（大米）' AFTER processor,
  ADD COLUMN harvest_date DATE DEFAULT NULL COMMENT '收割时间（大米）' AFTER warehouse,
  ADD COLUMN process_date DATE DEFAULT NULL COMMENT '加工时间（大米）' AFTER harvest_date;

UPDATE certificates SET product_type = 'yangmei' WHERE product_type IS NULL OR product_type = '';
