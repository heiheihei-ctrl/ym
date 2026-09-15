SET NAMES utf8mb4;

-- ============================================================
-- 见证业务菜单重整：一级「见证业务」目录 + 按操作顺序的二级页面
-- 执行后请重新登录以刷新路由
-- ============================================================

-- 1) 创建一级目录（若不存在）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '见证业务', 0, 1, 'ym', NULL, 1, 0, 'M', '0', '0', '', 'form', 'admin', sysdate(), '农产品见证溯源业务菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'ym' AND menu_type = 'M' AND parent_id = 0);

SET @ymDirId = (SELECT menu_id FROM sys_menu WHERE path = 'ym' AND menu_type = 'M' AND parent_id = 0 LIMIT 1);

-- 2) 挂到目录下，并按日常操作流排序、统一命名与图标
--    查询码 → 批次 → 见证书 → 产品 → 产地 → 律师 → 站点配置

UPDATE sys_menu SET
  parent_id = @ymDirId, order_num = 1, path = 'verificationCode', component = 'ym/verificationCode/index',
  menu_type = 'C', visible = '0', status = '0',
  menu_name = '查询码管理', perms = 'ym:verificationCode:list', icon = 'validCode',
  is_frame = 1
WHERE menu_id = 2003 OR (component = 'ym/verificationCode/index' AND menu_type = 'C');

UPDATE sys_menu SET
  parent_id = @ymDirId, order_num = 2, path = 'batch', component = 'ym/batch/index',
  menu_type = 'C', visible = '0', status = '0',
  menu_name = '批次管理', perms = 'ym:batch:list', icon = 'list',
  is_frame = 1
WHERE menu_id = 2005 OR (component = 'ym/batch/index' AND menu_type = 'C');

UPDATE sys_menu SET
  parent_id = @ymDirId, order_num = 3, path = 'certificate', component = 'ym/certificate/index',
  menu_type = 'C', visible = '0', status = '0',
  menu_name = '见证书', perms = 'ym:certificate:list', icon = 'documentation',
  is_frame = 1
WHERE menu_id = 2000 OR (component = 'ym/certificate/index' AND menu_type = 'C' AND path IN ('certificate', 'index'));

UPDATE sys_menu SET
  parent_id = @ymDirId, order_num = 4, path = 'product', component = 'ym/product/index',
  menu_type = 'C', visible = '0', status = '0',
  menu_name = '产品管理', perms = 'ym:product:list', icon = 'shopping',
  is_frame = 1
WHERE menu_id = 2012 OR (component = 'ym/product/index' AND menu_type = 'C');

UPDATE sys_menu SET
  parent_id = @ymDirId, order_num = 5, path = 'origin', component = 'ym/origin/index',
  menu_type = 'C', visible = '0', status = '0',
  menu_name = '产地管理', perms = 'ym:origin:list', icon = 'guide',
  is_frame = 1
WHERE menu_id = 2002 OR (component = 'ym/origin/index' AND menu_type = 'C');

UPDATE sys_menu SET
  parent_id = @ymDirId, order_num = 6, path = 'lawyer', component = 'ym/lawyer/index',
  menu_type = 'C', visible = '0', status = '0',
  menu_name = '律师信息', perms = 'ym:lawyer:list', icon = 'peoples',
  is_frame = 1
WHERE menu_id = 2001 OR (component = 'ym/lawyer/index' AND menu_type = 'C');

UPDATE sys_menu SET
  parent_id = @ymDirId, order_num = 7, path = 'siteConfig', component = 'ym/siteConfig/index',
  menu_type = 'C', visible = '0', status = '0',
  menu_name = '站点配置', perms = 'ym:siteConfig:query', icon = 'edit',
  is_frame = 1
WHERE menu_id = 2004 OR (component = 'ym/siteConfig/index' AND menu_type = 'C');

-- 若曾残留「见证书」子页面菜单（parent 挂在旧证书下），清掉避免重复
DELETE FROM sys_role_menu WHERE menu_id IN (
  SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE parent_id = 2000 AND component = 'ym/certificate/index') t
);
DELETE FROM sys_menu WHERE parent_id = 2000 AND component = 'ym/certificate/index';

-- 3) 补齐按钮权限（F），已存在则跳过
-- 见证书
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '见证书查询', 2000, 1, '', '', 1, 0, 'F', '0', '0', 'ym:certificate:query', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:certificate:query');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '见证书新增', 2000, 2, '', '', 1, 0, 'F', '0', '0', 'ym:certificate:add', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:certificate:add');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '见证书修改', 2000, 3, '', '', 1, 0, 'F', '0', '0', 'ym:certificate:edit', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:certificate:edit');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '见证书删除', 2000, 4, '', '', 1, 0, 'F', '0', '0', 'ym:certificate:remove', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:certificate:remove');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '见证书导出', 2000, 5, '', '', 1, 0, 'F', '0', '0', 'ym:certificate:export', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:certificate:export');

UPDATE sys_menu SET menu_name = '见证书列表', parent_id = 2000, order_num = 0
WHERE perms = 'ym:certificate:list' AND menu_type = 'F';

-- 查询码
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '查询码查询', 2003, 1, '', '', 1, 0, 'F', '0', '0', 'ym:verificationCode:list', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:verificationCode:list' AND menu_type = 'F');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '查询码生成', 2003, 2, '', '', 1, 0, 'F', '0', '0', 'ym:verificationCode:generate', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:verificationCode:generate');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '查询码删除', 2003, 3, '', '', 1, 0, 'F', '0', '0', 'ym:verificationCode:remove', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:verificationCode:remove');

-- 产地
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产地查询', 2002, 1, '', '', 1, 0, 'F', '0', '0', 'ym:origin:query', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:origin:query');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产地新增', 2002, 2, '', '', 1, 0, 'F', '0', '0', 'ym:origin:add', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:origin:add');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产地修改', 2002, 3, '', '', 1, 0, 'F', '0', '0', 'ym:origin:edit', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:origin:edit');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产地删除', 2002, 4, '', '', 1, 0, 'F', '0', '0', 'ym:origin:remove', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:origin:remove');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产地导出', 2002, 5, '', '', 1, 0, 'F', '0', '0', 'ym:origin:export', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:origin:export');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产地列表', 2002, 0, '', '', 1, 0, 'F', '0', '0', 'ym:origin:list', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:origin:list' AND menu_type = 'F');

-- 律师
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '律师列表', 2001, 0, '', '', 1, 0, 'F', '0', '0', 'ym:lawyer:list', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:lawyer:list' AND menu_type = 'F');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '律师查询', 2001, 1, '', '', 1, 0, 'F', '0', '0', 'ym:lawyer:query', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:lawyer:query');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '律师新增', 2001, 2, '', '', 1, 0, 'F', '0', '0', 'ym:lawyer:add', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:lawyer:add');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '律师修改', 2001, 3, '', '', 1, 0, 'F', '0', '0', 'ym:lawyer:edit', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:lawyer:edit');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '律师删除', 2001, 4, '', '', 1, 0, 'F', '0', '0', 'ym:lawyer:remove', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:lawyer:remove');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '律师导出', 2001, 5, '', '', 1, 0, 'F', '0', '0', 'ym:lawyer:export', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:lawyer:export');

-- 站点配置
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '站点查询', 2004, 1, '', '', 1, 0, 'F', '0', '0', 'ym:siteConfig:query', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:siteConfig:query' AND menu_type = 'F');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '站点修改', 2004, 2, '', '', 1, 0, 'F', '0', '0', 'ym:siteConfig:edit', '#', 'admin', sysdate(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'ym:siteConfig:edit');

-- 产品：禁止新增/删除按钮（页面与接口已限制，菜单也隐藏）
UPDATE sys_menu SET status = '1', visible = '1'
WHERE perms IN ('ym:product:add', 'ym:product:remove') AND menu_type = 'F';

-- 4) 把目录与全部业务菜单/按钮授权给已拥有任一 ym 菜单的角色
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @ymDirId
FROM sys_role_menu rm
INNER JOIN sys_menu m ON m.menu_id = rm.menu_id
WHERE (m.perms LIKE 'ym:%' OR m.component LIKE 'ym/%')
  AND @ymDirId IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = @ymDirId);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM (
  SELECT DISTINCT rm.role_id
  FROM sys_role_menu rm
  INNER JOIN sys_menu mm ON mm.menu_id = rm.menu_id
  WHERE mm.perms LIKE 'ym:%' OR mm.component LIKE 'ym/%' OR mm.menu_id = @ymDirId
) r
CROSS JOIN sys_menu m
WHERE (m.perms LIKE 'ym:%' OR m.component LIKE 'ym/%' OR m.menu_id = @ymDirId)
  AND m.status = '0'
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu x WHERE x.role_id = r.role_id AND x.menu_id = m.menu_id);

-- 5) 系统管理目录排到见证业务之后，避免抢视觉
UPDATE sys_menu SET order_num = 10 WHERE menu_id = 1 AND parent_id = 0;
UPDATE sys_menu SET order_num = 11 WHERE menu_id = 2 AND parent_id = 0;
