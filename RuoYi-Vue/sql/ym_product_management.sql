-- 产品管理（杨梅、五常大米等）
-- 导入请使用：mysql ... --default-character-set=utf8mb4 < ym_product_management.sql
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS ym_products (
  id            INT          NOT NULL AUTO_INCREMENT COMMENT '主键',
  product_code  VARCHAR(32)  NOT NULL COMMENT '产品编码，如 yangmei / rice',
  product_name  VARCHAR(100) NOT NULL COMMENT '产品名称',
  sort_order    INT          DEFAULT 0 COMMENT '排序',
  status        INT          DEFAULT 1 COMMENT '1启用 0禁用',
  remark        VARCHAR(500) DEFAULT NULL COMMENT '备注',
  created_at    DATETIME     DEFAULT NULL,
  updated_at    DATETIME     DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='见证产品';

INSERT INTO ym_products (product_code, product_name, sort_order, status, created_at, updated_at)
SELECT 'yangmei', '杨梅', 1, 1, sysdate(), sysdate() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ym_products WHERE product_code = 'yangmei');

INSERT INTO ym_products (product_code, product_name, sort_order, status, created_at, updated_at)
SELECT 'rice', '五常大米', 2, 1, sysdate(), sysdate() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM ym_products WHERE product_code = 'rice');

-- 菜单（与证书等同级的顶级菜单）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产品管理', 0, 0, 'product', 'ym/product/index', 1, 0, 'C', '0', '0', 'ym:product:list', 'shopping', 'admin', sysdate(), '见证产品维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'product' AND component = 'ym/product/index');

SET @productMenuId = (SELECT menu_id FROM sys_menu WHERE path = 'product' AND component = 'ym/product/index' LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产品查询', @productMenuId, 1, '', '', 1, 0, 'F', '0', '0', 'ym:product:query', '#', 'admin', sysdate(), ''
FROM DUAL WHERE @productMenuId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:product:query');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产品新增', @productMenuId, 2, '', '', 1, 0, 'F', '0', '0', 'ym:product:add', '#', 'admin', sysdate(), ''
FROM DUAL WHERE @productMenuId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:product:add');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产品修改', @productMenuId, 3, '', '', 1, 0, 'F', '0', '0', 'ym:product:edit', '#', 'admin', sysdate(), ''
FROM DUAL WHERE @productMenuId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:product:edit');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产品删除', @productMenuId, 4, '', '', 1, 0, 'F', '0', '0', 'ym:product:remove', '#', 'admin', sysdate(), ''
FROM DUAL WHERE @productMenuId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:product:remove');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产品导出', @productMenuId, 5, '', '', 1, 0, 'F', '0', '0', 'ym:product:export', '#', 'admin', sysdate(), ''
FROM DUAL WHERE @productMenuId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:product:export');

-- 管理员角色授权（role_id=1）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id FROM sys_menu m
WHERE (m.path = 'product' AND m.component = 'ym/product/index') OR m.perms LIKE 'ym:product:%'
AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id);
