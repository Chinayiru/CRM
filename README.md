# Cordys CRM（自研复刻版）

参考飞致云开源 Cordys CRM 的功能设计、自研实现的轻量级客户关系管理系统（MVP）。
原创实现，不复制参考项目源码、Logo 与文案，不追求 100% 还原。

## 技术栈

- 后端：Spring Boot 4.1.1（Java 21）、MyBatis-Plus 3.5.17、Sa-Token（认证，后续接入）
- 数据库：MySQL 8、Redis（缓存，后续接入）
- 前端：Vue 3 + Vite + Element Plus（独立仓库，后续接入）

## 统一响应格式

所有接口（含异常响应）都返回同一结构，前端统一按 `code` 判定结果：

```json
{
  "code": 200,
  "message": "success",
  "path": null,
  "data": { "id": 1, "name": "示例客户" },
  "errors": [],
  "timestamp": "2026-10-14T11:40:21"
}
```

失败时 `data` 为 `null`，`message` 为可读中文原因，字段级校验失败还会带 `errors`：

```json
{
  "code": 400,
  "message": "name: 客户名称不能为空",
  "path": "/api/customers",
  "data": null,
  "errors": [{ "field": "name", "message": "客户名称不能为空" }],
  "timestamp": "2026-10-14T11:40:21"
}
```

状态码取值集中在 `common/ResultCode`，HTTP 状态码随业务码返回：

| 业务码 | 含义 | 典型场景 |
| --- | --- | --- |
| 200 | 成功 | 查询、新增、修改、删除均返回 200 |
| 400 | 参数不合法 | 校验失败、JSON 格式错误、业务规则不满足 |
| 401 | 未认证 | Sa-Token 接入后使用 |
| 403 | 权限不足 | Sa-Token 接入后使用 |
| 404 | 资源不存在 | ID 不存在；引用的关联数据（如负责人）不存在 |
| 409 | 数据冲突 | 同名部门、用户名已存在、重复授权 |
| 500 | 服务端异常 | 兜底，响应体不暴露堆栈与 SQL 细节 |

> 说明：外键约束失败统一映射为 404、唯一索引冲突映射为 409，不再返回 500。

## 环境配置

应用提供 `test`（默认）和 `prod` 两套 Spring Profile。数据库凭据通过 `DB_USERNAME`、`DB_PASSWORD` 环境变量提供，不要写入 YAML 文件。

### 测试环境

先创建开发与测试数据库（脚本同时建 `cordys_crm` 与 `cordys_crm_test`）：

```bash
mysql -u root -p < src/main/resources/db/create-database.sql
```

测试环境使用独立的 `cordys_crm_test` 数据库（端口 8082），启动时自动执行 `schema.sql` 建表与 `test-data.sql` 种子数据。PowerShell 示例：

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

## 运行测试

测试直接跑在 `cordys_crm_test` 上，覆盖统一响应契约、异常映射、数据初始化与校验约束：

```powershell
$env:DB_PASSWORD = "你的数据库密码"
.\mvnw.cmd test
```

需要 JDK 21（`JAVA_HOME` 指向 JDK 21）。`mvnw` 命令行构建还依赖两项 pom 配置，缺少会导致编译失败：

- `maven-compiler-plugin` 的 `-proc:full`：javac 21 起注解处理默认关闭，不加 Lombok 不会生成 getter/setter。
- `spring-boot-starter-webmvc-test`：Spring Boot 4 把 Web MVC 测试切片从 `spring-boot-test-autoconfigure` 拆出，`@AutoConfigureMockMvc` 位于 `org.springframework.boot.webmvc.test.autoconfigure` 包。

测试数据位于 `src/test/resources/db/test-data.sql`，脚本幂等，可重复执行。

## 目录结构

```
src/main/java/com/example/cordyscrm/
├── common/               # ApiResponse、ResultCode、GlobalExceptionHandler
├── controller/           # REST 接口
├── dto/                  # 请求参数与校验约束
├── entity/ mapper/ service/
src/main/resources/db/
├── create-database.sql   # 建库脚本（手动执行一次）
└── schema.sql            # 建表 + 字典种子
src/test/resources/db/
└── test-data.sql         # 测试环境种子数据（仅 test profile 加载）
```
