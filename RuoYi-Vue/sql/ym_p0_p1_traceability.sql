-- 杨梅防伪溯源 P0/P1：批次、码绑定状态、扫码指纹
-- 在已有 verification / certificates 等表基础上执行

-- ---------- 批次 ----------
CREATE TABLE IF NOT EXISTS ym_batches (
  id            INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
  batch_no      VARCHAR(64)  NOT NULL COMMENT '批次号',
  name          VARCHAR(200) NOT NULL COMMENT '批次名称，如 XX果园A批杨梅',
  origin        VARCHAR(200) DEFAULT NULL COMMENT '产地',
  pick_date     DATE         DEFAULT NULL COMMENT '采摘日期',
  video_url     VARCHAR(500) DEFAULT NULL COMMENT '跟采/溯源视频地址',
  lawyer_name   VARCHAR(100) DEFAULT NULL COMMENT '跟采/鉴证律师',
  remark        VARCHAR(500) DEFAULT NULL COMMENT '备注',
  status        INT          DEFAULT 1 COMMENT '1启用 0禁用',
  created_at    DATETIME     DEFAULT NULL COMMENT '创建时间',
  updated_at    DATETIME     DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_batch_no (batch_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='杨梅采摘/鉴证批次';

-- ---------- 查询码：绑定状态机 ----------
ALTER TABLE verification
  ADD COLUMN certificate_id INT DEFAULT NULL COMMENT '绑定的见证书ID' AFTER code_url,
  ADD COLUMN bind_status    INT DEFAULT 0 COMMENT '0未绑定 1已绑定' AFTER certificate_id,
  ADD COLUMN bound_at       DATETIME DEFAULT NULL COMMENT '绑定时间' AFTER bind_status;

ALTER TABLE verification
  ADD INDEX idx_verification_cert (certificate_id),
  ADD INDEX idx_verification_bind (bind_status);

-- 回填历史：已挂在 certificates.code 上的码视为已绑定
UPDATE verification v
INNER JOIN certificates c ON c.code = v.id
SET v.certificate_id = c.id,
    v.bind_status = 1,
    v.bound_at = IFNULL(c.created_at, NOW())
WHERE v.bind_status = 0 OR v.certificate_id IS NULL;

-- ---------- 证书挂批次 ----------
ALTER TABLE certificates
  ADD COLUMN batch_id INT DEFAULT NULL COMMENT '所属批次ID' AFTER code;

ALTER TABLE certificates
  ADD INDEX idx_certificates_batch (batch_id);

-- ---------- 扫码指纹日志 ----------
CREATE TABLE IF NOT EXISTS ym_scan_logs (
  id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  verification_id  INT          NOT NULL COMMENT '查询码ID',
  code             VARCHAR(64)  NOT NULL COMMENT '查询码文本',
  scan_ip          VARCHAR(64)  DEFAULT NULL COMMENT '扫码IP',
  user_agent       VARCHAR(500) DEFAULT NULL COMMENT 'UA',
  scanned_at       DATETIME     NOT NULL COMMENT '扫码时间',
  PRIMARY KEY (id),
  KEY idx_scan_verification (verification_id),
  KEY idx_scan_code (code),
  KEY idx_scan_time (scanned_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费者扫码指纹日志';

-- ---------- 菜单（批次管理：挂到与证书管理相同的父菜单下） ----------
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '批次管理', m.parent_id, IFNULL(m.order_num, 0) + 1, 'batch', 'ym/batch/index', 1, 0, 'C', '0', '0', 'ym:batch:list', 'list', 'admin', sysdate(), '杨梅批次管理'
FROM sys_menu m
WHERE m.perms = 'ym:certificate:list' AND m.menu_type = 'C'
LIMIT 1;

SET @batchMenuId = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '批次查询', @batchMenuId, 1, '', '', 1, 0, 'F', '0', '0', 'ym:batch:query', '#', 'admin', sysdate(), '' FROM DUAL WHERE @batchMenuId > 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '批次新增', @batchMenuId, 2, '', '', 1, 0, 'F', '0', '0', 'ym:batch:add', '#', 'admin', sysdate(), '' FROM DUAL WHERE @batchMenuId > 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '批次修改', @batchMenuId, 3, '', '', 1, 0, 'F', '0', '0', 'ym:batch:edit', '#', 'admin', sysdate(), '' FROM DUAL WHERE @batchMenuId > 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '批次删除', @batchMenuId, 4, '', '', 1, 0, 'F', '0', '0', 'ym:batch:remove', '#', 'admin', sysdate(), '' FROM DUAL WHERE @batchMenuId > 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '批次导出', @batchMenuId, 5, '', '', 1, 0, 'F', '0', '0', 'ym:batch:export', '#', 'admin', sysdate(), '' FROM DUAL WHERE @batchMenuId > 0;
