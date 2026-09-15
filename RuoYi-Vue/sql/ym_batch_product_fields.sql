SET NAMES utf8mb4;

-- 批次：产品类型 + 大米专属字段（厂家、仓库、收割/加工时间）
ALTER TABLE ym_batches
  ADD COLUMN product_type VARCHAR(32) DEFAULT 'yangmei' COMMENT '产品编码 yangmei/rice' AFTER name,
  ADD COLUMN processor VARCHAR(200) DEFAULT NULL COMMENT '加工包装厂家' AFTER origin,
  ADD COLUMN warehouse VARCHAR(200) DEFAULT NULL COMMENT '仓库' AFTER processor,
  ADD COLUMN harvest_date DATE DEFAULT NULL COMMENT '收割时间' AFTER warehouse,
  ADD COLUMN process_date DATE DEFAULT NULL COMMENT '加工时间' AFTER harvest_date;

UPDATE ym_batches SET product_type = 'yangmei' WHERE product_type IS NULL OR product_type = '';

-- 批次管理：侧栏显示（与验证码管理同级）
UPDATE sys_menu SET visible = '0' WHERE path = 'batch' AND component = 'ym/batch/index';
-- 产品管理：侧栏展示，仅允许修改（不可新增/删除见控制器）
UPDATE sys_menu SET visible = '0' WHERE path = 'product' AND component = 'ym/product/index';
