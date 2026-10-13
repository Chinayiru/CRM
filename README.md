# Cordys CRM（自研复刻版）

参考飞致云开源 Cordys CRM 的功能设计、自研实现的轻量级客户关系管理系统（MVP）。
原创实现，不复制参考项目源码、Logo 与文案，不追求 100% 还原。

## 技术栈

- 后端：Spring Boot 4.1.1（Java 25）、MyBatis-Plus 3.5.17、Sa-Token（认证，后续接入）
- 数据库：MySQL 8、Redis（缓存，后续接入）
- 前端：Vue 3 + Vite + Element Plus（独立仓库，后续接入）

## 快速开始

### 1. 初始化数据库

MySQL 8 环境准备就绪后，先创建数据库（一次性操作）：

```bash
mysql -u root -p < src/main/resources/db/create-database.sql
```

### 2. 启动应用自动建表

配置 `src/main/resources/application.properties` 中的数据库账号密码，然后启动应用：

```bash
./mvnw spring-boot:run
```

首次启动会自动执行 `db/schema.sql`：创建系统表、业务表（线索、客户、商机、合同、跟进记录、操作日志）并写入字典种子数据。脚本使用 `IF NOT EXISTS`，可重复执行。

### 3. 验证

```sql
SHOW TABLES FROM cordys_crm;
```

## 目录结构

```
src/main/resources/db/
├── create-database.sql   # 建库脚本（手动执行一次）
└── schema.sql            # 建表 + 字典种子（应用启动自动执行）
```
