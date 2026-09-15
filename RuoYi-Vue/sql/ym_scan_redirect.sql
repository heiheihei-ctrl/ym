-- 扫码证书页：可配置跳转地址与等待秒数
ALTER TABLE site_config
  ADD COLUMN redirect_url VARCHAR(500) DEFAULT NULL COMMENT '扫码证书页跳转地址，空则不跳转' AFTER address,
  ADD COLUMN redirect_delay_seconds INT DEFAULT 6 COMMENT '跳转等待秒数，默认6' AFTER redirect_url;
