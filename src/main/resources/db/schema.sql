CREATE TABLE IF NOT EXISTS `sys_dept` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '部门主键',
  `parent_id` BIGINT UNSIGNED NULL COMMENT '上级部门；根部门为空',
  `dept_name` VARCHAR(100) NOT NULL COMMENT '部门名称',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` ENUM('ENABLED','DISABLED') NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  `created_by` BIGINT UNSIGNED NULL COMMENT '创建人',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间UTC',
  `updated_by` BIGINT UNSIGNED NULL COMMENT '更新人',
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间UTC',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_dept_parent_name` (`parent_id`,`dept_name`,`deleted`),
  KEY `idx_sys_dept_parent` (`parent_id`,`status`,`deleted`),
  CONSTRAINT `fk_sys_dept_parent` FOREIGN KEY (`parent_id`) REFERENCES `sys_dept` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='组织部门';

CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户主键',
  `username` VARCHAR(64) NOT NULL COMMENT '登录用户名',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '不可逆密码哈希，不存明文密码',
  `nickname` VARCHAR(100) NULL COMMENT '显示名称',
  `phone` VARCHAR(32) NULL COMMENT '联系电话；按权限展示',
  `email` VARCHAR(254) NULL COMMENT '邮箱',
  `department_id` BIGINT UNSIGNED NULL COMMENT '所属部门',
  `status` ENUM('ENABLED','DISABLED') NOT NULL DEFAULT 'ENABLED' COMMENT '账号状态',
  `approval_status` ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'APPROVED' COMMENT '注册审核状态',
  `created_by` BIGINT UNSIGNED NULL COMMENT '创建人',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间UTC',
  `updated_by` BIGINT UNSIGNED NULL COMMENT '更新人',
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间UTC',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  KEY `idx_sys_user_department_status` (`department_id`,`status`,`deleted`),
  KEY `idx_sys_user_approval` (`approval_status`,`created_at`,`deleted`),
  CONSTRAINT `fk_sys_user_department` FOREIGN KEY (`department_id`) REFERENCES `sys_dept` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='系统用户';

CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色主键',
  `role_name` VARCHAR(100) NOT NULL COMMENT '角色名称',
  `role_key` VARCHAR(64) NOT NULL COMMENT '角色标识',
  `data_scope` ENUM('SELF','DEPT','ALL') NOT NULL DEFAULT 'SELF' COMMENT '数据范围：本人、本部门、全部',
  `remark` VARCHAR(500) NULL COMMENT '备注',
  `status` ENUM('ENABLED','DISABLED') NOT NULL DEFAULT 'ENABLED' COMMENT '角色状态',
  `created_by` BIGINT UNSIGNED NULL COMMENT '创建人',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间UTC',
  `updated_by` BIGINT UNSIGNED NULL COMMENT '更新人',
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间UTC',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_key` (`role_key`),
  KEY `idx_sys_role_status` (`status`,`deleted`)
) ENGINE=InnoDB COMMENT='系统角色与数据范围';

CREATE TABLE IF NOT EXISTS `sys_menu` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '权限节点主键',
  `parent_id` BIGINT UNSIGNED NULL COMMENT '父节点；根节点为空',
  `menu_name` VARCHAR(100) NOT NULL COMMENT '菜单或权限名称',
  `menu_type` ENUM('DIRECTORY','MENU','BUTTON') NOT NULL COMMENT '目录、菜单或按钮权限',
  `path` VARCHAR(255) NULL COMMENT '前端路由路径',
  `permission_key` VARCHAR(150) NULL COMMENT '后端权限标识',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` ENUM('ENABLED','DISABLED') NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  `created_by` BIGINT UNSIGNED NULL COMMENT '创建人',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间UTC',
  `updated_by` BIGINT UNSIGNED NULL COMMENT '更新人',
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间UTC',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_menu_permission` (`permission_key`),
  KEY `idx_sys_menu_parent_sort` (`parent_id`,`sort_no`,`status`,`deleted`),
  CONSTRAINT `fk_sys_menu_parent` FOREIGN KEY (`parent_id`) REFERENCES `sys_menu` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='菜单及功能权限';

CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '授权时间UTC',
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `idx_sys_user_role_role` (`role_id`,`user_id`),
  CONSTRAINT `fk_sys_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT `fk_sys_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='用户角色关联';

CREATE TABLE IF NOT EXISTS `sys_role_menu` (
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT UNSIGNED NOT NULL COMMENT '菜单或权限ID',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '授权时间UTC',
  PRIMARY KEY (`role_id`,`menu_id`),
  KEY `idx_sys_role_menu_menu` (`menu_id`,`role_id`),
  CONSTRAINT `fk_sys_role_menu_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT `fk_sys_role_menu_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='角色菜单权限关联';

CREATE TABLE IF NOT EXISTS `sys_dict` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '字典项主键',
  `dict_type` VARCHAR(64) NOT NULL COMMENT '字典类型',
  `dict_label` VARCHAR(100) NOT NULL COMMENT '显示名称',
  `dict_value` VARCHAR(100) NOT NULL COMMENT '字典值',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` ENUM('ENABLED','DISABLED') NOT NULL DEFAULT 'ENABLED' COMMENT '状态',
  `created_by` BIGINT UNSIGNED NULL COMMENT '创建人',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间UTC',
  `updated_by` BIGINT UNSIGNED NULL COMMENT '更新人',
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间UTC',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_dict_type_value` (`dict_type`,`dict_value`,`deleted`),
  KEY `idx_sys_dict_type_status_sort` (`dict_type`,`status`,`sort_no`,`deleted`)
) ENGINE=InnoDB COMMENT='数据字典';

CREATE TABLE IF NOT EXISTS `clue` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '线索主键',
  `name` VARCHAR(150) NOT NULL COMMENT '线索名称',
  `phone` VARCHAR(32) NULL COMMENT '联系电话',
  `source` VARCHAR(100) NULL COMMENT '来源字典值',
  `owner_id` BIGINT UNSIGNED NOT NULL COMMENT '负责人用户ID',
  `status` ENUM('NEW','FOLLOWING','CONVERTED','DISCARDED') NOT NULL DEFAULT 'NEW' COMMENT '线索状态',
  `remark` TEXT NULL COMMENT '备注',
  `created_by` BIGINT UNSIGNED NULL COMMENT '创建人',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间UTC',
  `updated_by` BIGINT UNSIGNED NULL COMMENT '更新人',
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间UTC',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_clue_owner_status_created` (`owner_id`,`status`,`created_at`,`deleted`),
  KEY `idx_clue_status_created` (`status`,`created_at`,`deleted`),
  KEY `idx_clue_phone` (`phone`),
  CONSTRAINT `fk_clue_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='销售线索';

CREATE TABLE IF NOT EXISTS `customer` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '客户主键',
  `name` VARCHAR(200) NOT NULL COMMENT '客户名称',
  `customer_type` VARCHAR(100) NULL COMMENT '客户类型字典值',
  `contact` VARCHAR(100) NULL COMMENT '联系人',
  `phone` VARCHAR(32) NULL COMMENT '联系电话',
  `industry` VARCHAR(100) NULL COMMENT '行业字典值',
  `owner_id` BIGINT UNSIGNED NOT NULL COMMENT '负责人用户ID',
  `source` VARCHAR(100) NULL COMMENT '来源字典值',
  `address` VARCHAR(500) NULL COMMENT '地址',
  `status` ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '客户状态',
  `source_clue_id` BIGINT UNSIGNED NULL COMMENT '转化来源线索；手工客户为空',
  `created_by` BIGINT UNSIGNED NULL COMMENT '创建人',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间UTC',
  `updated_by` BIGINT UNSIGNED NULL COMMENT '更新人',
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间UTC',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_source_clue` (`source_clue_id`),
  KEY `idx_customer_owner_status_created` (`owner_id`,`status`,`created_at`,`deleted`),
  KEY `idx_customer_industry` (`industry`,`deleted`),
  KEY `idx_customer_name` (`name`),
  CONSTRAINT `fk_customer_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT `fk_customer_source_clue` FOREIGN KEY (`source_clue_id`) REFERENCES `clue` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='客户档案';

CREATE TABLE IF NOT EXISTS `opportunity` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商机主键',
  `name` VARCHAR(200) NOT NULL COMMENT '商机名称',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '关联客户ID',
  `amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '预计金额，单位元',
  `stage` ENUM('INITIAL_CONTACT','NEEDS_CONFIRMED','PROPOSAL','NEGOTIATION','WON','LOST') NOT NULL DEFAULT 'INITIAL_CONTACT' COMMENT '商机阶段',
  `expect_time` DATE NULL COMMENT '预计成交日期',
  `win_rate` TINYINT UNSIGNED NULL COMMENT '赢率百分比0至100',
  `lost_reason` VARCHAR(1000) NULL COMMENT '输单原因',
  `closed_at` DATETIME(3) NULL COMMENT '赢单或输单时间UTC',
  `owner_id` BIGINT UNSIGNED NOT NULL COMMENT '负责人用户ID',
  `created_by` BIGINT UNSIGNED NULL COMMENT '创建人',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间UTC',
  `updated_by` BIGINT UNSIGNED NULL COMMENT '更新人',
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间UTC',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_opportunity_customer` (`customer_id`,`deleted`),
  KEY `idx_opportunity_owner_stage` (`owner_id`,`stage`,`deleted`),
  KEY `idx_opportunity_stage_expect` (`stage`,`expect_time`,`deleted`),
  CONSTRAINT `fk_opportunity_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT `fk_opportunity_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT `ck_opportunity_amount` CHECK (`amount` >= 0),
  CONSTRAINT `ck_opportunity_win_rate` CHECK (`win_rate` IS NULL OR `win_rate` <= 100),
  CONSTRAINT `ck_opportunity_lost_reason` CHECK (`stage` <> 'LOST' OR `lost_reason` IS NOT NULL)
) ENGINE=InnoDB COMMENT='销售商机';

CREATE TABLE IF NOT EXISTS `contract` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '合同主键',
  `contract_no` VARCHAR(64) NOT NULL COMMENT '合同编号',
  `name` VARCHAR(200) NOT NULL COMMENT '合同名称',
  `customer_id` BIGINT UNSIGNED NOT NULL COMMENT '关联客户ID',
  `opportunity_id` BIGINT UNSIGNED NULL COMMENT '来源商机ID',
  `amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '合同金额，单位元',
  `sign_date` DATE NULL COMMENT '签订日期',
  `expire_date` DATE NULL COMMENT '到期日期',
  `status` ENUM('DRAFT','EFFECTIVE','IN_PROGRESS','COMPLETED','TERMINATED') NOT NULL DEFAULT 'DRAFT' COMMENT '合同状态',
  `terminate_reason` VARCHAR(1000) NULL COMMENT '终止原因',
  `created_by` BIGINT UNSIGNED NULL COMMENT '创建人',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间UTC',
  `updated_by` BIGINT UNSIGNED NULL COMMENT '更新人',
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间UTC',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contract_no` (`contract_no`),
  KEY `idx_contract_customer` (`customer_id`,`deleted`),
  KEY `idx_contract_opportunity` (`opportunity_id`,`deleted`),
  KEY `idx_contract_status_sign_date` (`status`,`sign_date`,`deleted`),
  CONSTRAINT `fk_contract_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT `fk_contract_opportunity` FOREIGN KEY (`opportunity_id`) REFERENCES `opportunity` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT `ck_contract_amount` CHECK (`amount` >= 0),
  CONSTRAINT `ck_contract_sign_date` CHECK (`status` = 'DRAFT' OR `sign_date` IS NOT NULL),
  CONSTRAINT `ck_contract_terminate_reason` CHECK (`status` <> 'TERMINATED' OR `terminate_reason` IS NOT NULL)
) ENGINE=InnoDB COMMENT='销售合同';

CREATE TABLE IF NOT EXISTS `follow_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '跟进记录主键',
  `target_type` ENUM('CUSTOMER','OPPORTUNITY') NOT NULL COMMENT '关联对象类型',
  `target_id` BIGINT UNSIGNED NOT NULL COMMENT '关联对象ID；由服务层校验存在性',
  `method` VARCHAR(100) NULL COMMENT '跟进方式字典值',
  `content` TEXT NOT NULL COMMENT '跟进内容',
  `next_time` DATETIME(3) NULL COMMENT '下次跟进时间UTC',
  `owner_id` BIGINT UNSIGNED NOT NULL COMMENT '跟进人用户ID',
  `created_by` BIGINT UNSIGNED NULL COMMENT '创建人',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间UTC',
  `updated_by` BIGINT UNSIGNED NULL COMMENT '更新人',
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间UTC',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_follow_target_time` (`target_type`,`target_id`,`created_at`,`deleted`),
  KEY `idx_follow_owner_next` (`owner_id`,`next_time`,`deleted`),
  CONSTRAINT `fk_follow_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='客户与商机跟进记录';

CREATE TABLE IF NOT EXISTS `sys_operation_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `operator_id` BIGINT UNSIGNED NULL COMMENT '操作人；系统动作可为空',
  `module` VARCHAR(100) NOT NULL COMMENT '业务模块',
  `action` VARCHAR(100) NOT NULL COMMENT '操作动作',
  `target_type` VARCHAR(100) NULL COMMENT '目标对象类型',
  `target_id` VARCHAR(100) NULL COMMENT '目标对象标识',
  `result` ENUM('SUCCESS','FAILURE') NOT NULL COMMENT '操作结果',
  `ip_address` VARCHAR(45) NULL COMMENT '客户端IP地址',
  `detail` JSON NULL COMMENT '脱敏后的操作摘要，不得记录密码或令牌',
  `duration_ms` INT UNSIGNED NULL COMMENT '耗时毫秒',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '操作时间UTC',
  PRIMARY KEY (`id`),
  KEY `idx_operation_log_time` (`created_at`),
  KEY `idx_operation_log_operator_time` (`operator_id`,`created_at`),
  KEY `idx_operation_log_target` (`target_type`,`target_id`,`created_at`)
) ENGINE=InnoDB COMMENT='不可变操作审计日志';

-- Minimal dictionary seed data; application roles/users are provisioned securely at first startup.
INSERT IGNORE INTO `sys_dict` (`dict_type`,`dict_label`,`dict_value`,`sort_no`) VALUES
('clue_source','网站','website',10),('clue_source','电话','phone',20),('clue_source','转介绍','referral',30),
('customer_type','企业客户','enterprise',10),('customer_type','个人客户','individual',20),
('follow_method','电话','phone',10),('follow_method','邮件','email',20),('follow_method','会议','meeting',30),('follow_method','其他','other',90);;
