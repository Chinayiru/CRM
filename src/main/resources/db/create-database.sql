-- Cordys CRM MVP schema, MySQL 8.0+
-- Run against the dedicated database only. All DATETIME values are stored in UTC.
CREATE DATABASE IF NOT EXISTS `cordys_crm`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS `cordys_crm_test`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE `cordys_crm`;
SET time_zone = '+00:00';;
