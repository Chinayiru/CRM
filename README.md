# Cordys CRM（自研复刻版）

参考飞致云开源 Cordys CRM 的功能设计、自研实现的轻量级客户关系管理系统（MVP）。
原创实现，不复制参考项目源码、Logo 与文案，不追求 100% 还原。

## 技术栈

- 后端：Spring Boot 4.1.1（Java 21）、MyBatis-Plus 3.5.17、Sa-Token（认证，后续接入）
- 数据库：MySQL 8、Redis（缓存，后续接入）
- 前端：Vue 3 + Vite + Element Plus（独立仓库，后续接入）

## 环境配置

应用提供 `test`（默认）和 `prod` 两套 Spring Profile。数据库凭据通过 `DB_USERNAME`、`DB_PASSWORD` 环境变量提供，不要写入 YAML 文件。

### 测试环境

先创建开发与测试数据库：

```bash
mysql -u root -p < src/main/resources/db/create-database.sql
```

测试环境使用独立的 `cordys_crm_test` 数据库，启动时自动执行 `schema.sql`。PowerShell 示例：

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "你的数据库密码"
.\mvnw.cmd spring-boot:run
```

也可以显式指定测试环境：

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=test"
```

### 正式环境

正式环境不会自动运行建表脚本。先按发布流程手动应用数据库变更，再通过环境变量配置正式数据库并启动：

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod"
$env:DB_URL = "jdbc:mysql://数据库主机:3306/cordys_crm?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=true"
$env:DB_USERNAME = "应用专用账号"
$env:DB_PASSWORD = "正式数据库密码"
java -jar target/CordysCRM-0.0.1-SNAPSHOT.jar
```

### 验证数据库

```sql
SHOW TABLES FROM cordys_crm_test;
```

## 目录结构

```
src/main/resources/db/
├── create-database.sql   # 建库脚本（手动执行一次）
└── schema.sql            # 建表 + 字典种子（仅测试环境自动执行）
```
