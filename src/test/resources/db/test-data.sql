-- 测试环境种子数据：仅由 test profile 加载（spring.sql.init.data-locations）
-- 脚本必须幂等，重复启动不能报错，也不能覆盖已有数据。
-- 所有业务表都有外键约束，因此负责人、客户、商机等关联数据必须成套存在。

-- 默认管理员仅用于测试环境，登录密码为 TestAdmin2026!；正式环境不得加载此脚本。
INSERT IGNORE INTO `sys_user` (`id`,`username`,`password_hash`,`nickname`,`status`,`deleted`)
VALUES (1,'admin','pbkdf2-sha256$210000$TXFuntVHSQj/s6XaNgiD6A$q0+IuOK7LT+5s3r/hi8dQfkmlZBBjNYTCMr3Bj1MCfo','测试管理员','ENABLED',0);

-- 默认部门
INSERT IGNORE INTO `sys_dept` (`id`,`parent_id`,`dept_name`,`sort_no`,`status`,`deleted`)
VALUES (1,NULL,'技术部',10,'ENABLED',0);

-- 默认角色（ALL 数据范围），并授予测试管理员
INSERT IGNORE INTO `sys_role` (`id`,`role_name`,`role_key`,`data_scope`,`status`,`deleted`)
VALUES (1,'系统管理员','admin','ALL','ENABLED',0);

INSERT IGNORE INTO `sys_user_role` (`user_id`,`role_id`) VALUES (1,1);
