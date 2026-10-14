-- Run once on databases created before registration approval was added.
-- Existing accounts are treated as approved; new registrations explicitly use PENDING.
ALTER TABLE `sys_user`
  ADD COLUMN `approval_status` ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'APPROVED'
  COMMENT '注册审核状态' AFTER `status`;

CREATE INDEX `idx_sys_user_approval`
  ON `sys_user` (`approval_status`,`created_at`,`deleted`);
