# OrderPilot

基于 Java / Spring Boot 的电商 ERP 实战项目，覆盖订单同步、库存管理、仓储履约与工单处理，结合 AI Agent 实现异常排查、证据追踪及人工确认后的业务操作，支持使用模拟数据运行完整流程。

**当前进度：后端工程骨架。** 已能编译、打包、启动和访问基础接口；业务模块尚未实现登录、订单或库存功能，Agent 模块尚未连接真实模型。前端随页面功能开发加入。

## 技术版本

| 技术 | 版本 / 说明 |
| --- | --- |
| Java | 17 |
| Spring Boot | 3.5.13 |
| Spring AI | 1.1.8，仅在 agent 引入 ChatClient 库 |
| Maven | Wrapper 固定为 3.9.8，也可使用本机 Maven 3.9.8 |
| MySQL | 8.x，可选启用；数据库名 `orderpilot` |

Spring AI 1.1.x 与 Spring Boot 3.5.x 配套使用。模型 SDK 需要时再引入，当前不要求 API Key。

## 目录

```text
orderpilot/
├── pom.xml                  父工程，统一依赖版本
├── orderpilot-boot/          唯一启动入口与应用配置
├── orderpilot-system/        系统管理
├── orderpilot-channel/       渠道管理
├── orderpilot-product/       商品管理
├── orderpilot-oms/           订单管理
├── orderpilot-inventory/     库存管理
├── orderpilot-wms/           仓储管理
├── orderpilot-workorder/     工单管理
├── orderpilot-agent/         Agent 排查与执行
├── docs/                    架构、数据库准备和验证记录
├── requests/orderpilot.http  可在 IDEA HTTP Client 中执行
├── mvnw / mvnw.cmd           Maven Wrapper
└── .mvn/wrapper/             Wrapper 版本配置
```

业务模块是普通 jar；boot 引用八个业务模块并生成可运行 jar。开发时从根目录构建全部模块，IDEA 中从根 `pom.xml` 导入即可。

## 最快启动：不需要数据库和模型密钥

在项目根目录打开 PowerShell。以下 JDK 路径对应当前开发电脑，其他电脑改成自己的 JDK 17 路径：

```powershell
$env:JAVA_HOME = 'D:\develop\jdk17'
.\mvnw.cmd clean verify
& "$env:JAVA_HOME\bin\java.exe" -jar .\orderpilot-boot\target\orderpilot.jar
```

首次使用 Wrapper 会下载 Maven 和项目依赖，需要网络。已有 Maven 时可以直接用 `mvn clean verify`；Wrapper 不要求 Maven 已加入 PATH。

其他系统：

```bash
sh ./mvnw clean verify
java -jar orderpilot-boot/target/orderpilot.jar
```

成功启动后访问：

| 地址 | 预期结果 |
| --- | --- |
| `http://localhost:8080/actuator/health` | `{"status":"UP"}` |
| `http://localhost:8080/api/system/info` | name 为 OrderPilot，stage 为 bootstrap |
| `http://localhost:8080/actuator/info` | 应用名称与版本 |

默认监听 `127.0.0.1:8080`，根路径 `/` 没有网页，返回 404 是正常的。修改端口可设置 `$env:SERVER_PORT = '8081'`。结束运行按 `Ctrl+C`。

`local` 是默认运行模式，没有数据库连接，也不会发起模型调用。此模式的健康状态仅代表应用基础服务可用，不代表数据库或 AI 已接通。

## IDEA 启动

1. 用 IDEA 打开根目录，加载根 `pom.xml` 的 Maven 工程。
2. Project SDK、Maven Importer/Runner JDK 均选 JDK 17。
3. 运行 `orderpilot-boot/src/main/java/com/orderpilot/OrderPilotApplication.java`。
4. 默认使用 local profile；运行上述三个接口验证。

不要对每个业务模块分别新建 Spring Boot 启动类。它们由 boot 模块统一扫描和装配。

## 启用 MySQL

数据库连接是独立的可选步骤，不影响默认骨架启动。

1. 在 MySQL 8.x 中手动执行 [建库 SQL](docs/database/00-create-database.sql)，创建 `orderpilot`。脚本不建业务表、不修改已有数据。
2. 使用已有授权的数据库账号，在运行进程的环境变量中填写连接信息。
3. 单独激活 `mysql` profile。不要同时启用 `local,mysql`，local 会排除数据源。

```powershell
$env:SPRING_PROFILES_ACTIVE = 'mysql'
$env:DB_HOST = '127.0.0.1'
$env:DB_PORT = '3306'
$env:DB_NAME = 'orderpilot'
$env:DB_USERNAME = '你的数据库用户名'
# 在 IDEA Run Configuration 的环境变量或当前进程环境中设置真实 DB_PASSWORD。
# 不要把真实密码写入 README、Git 跟踪的 YAML 或提交到仓库。
& "$env:JAVA_HOME\bin\java.exe" -jar .\orderpilot-boot\target\orderpilot.jar
```

| 环境变量 | 默认值 / 要求 |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | 未设置时默认 local；连接数据库设置 mysql |
| `DB_HOST` | `127.0.0.1` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `orderpilot` |
| `DB_USERNAME` | mysql 模式必须填写 |
| `DB_PASSWORD` | mysql 模式必须填写 |
| `SERVER_PORT` | `8080` |
| `SERVER_ADDRESS` | `127.0.0.1` |

mysql 模式下 `/actuator/health` 包含数据库连接检查；连接失败会返回 DOWN / HTTP 503，不会伪装成已接通。
项目不会自动读取 `.env`。环境变量需传给 Java 进程；IDEA 的运行环境与终端环境应分别配置。
切回 local 时，将 `SPRING_PROFILES_ACTIVE` 改成 `local` 即可。

## 继续开发

已确认范围与模块职责见 [架构说明](docs/architecture.md)。数据库表前缀约定为 `sys_`、`channel_`、`product_`、`oms_`、`inv_`、`wms_`、`wo_`、`agent_`。

接口返回普通 JSON，Spring MVC 错误响应使用 ProblemDetail（HTTP 400/404/405 等）。目前没有自定义通用 Result、登录认证或统一业务异常体系；这些随第一项业务功能确定，避免把尚未实现的能力包装成完成状态。

验证记录见 [docs/verification.md](docs/verification.md)。
