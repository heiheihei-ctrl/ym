SET NAMES utf8mb4;

-- 还原证书子菜单 / Tab 结构调整（恢复为独立一级菜单布局）

UPDATE sys_menu SET parent_id = 2000
WHERE menu_id = 2011 AND parent_id = 2018;

DELETE FROM sys_role_menu WHERE menu_id = 2018;
DELETE FROM sys_menu WHERE menu_id = 2018;

UPDATE sys_menu
SET menu_type = 'C', component = 'ym/certificate/index', path = 'certificate'
WHERE menu_id = 2000;

UPDATE sys_menu
SET parent_id = 0, order_num = 0, path = 'verificationCode', visible = '0'
WHERE menu_id = 2003;

UPDATE sys_menu
SET parent_id = 0, order_num = 0, path = 'batch', visible = '1'
WHERE menu_id = 2005;
