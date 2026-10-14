# 框架验证记录

验证日期：2026-10-14。

## 构建与集成测试

环境：Windows、Java 17.0.11、Maven Wrapper 3.3.2 / Maven 3.9.8。

执行 `mvnw.cmd clean verify`，验证时额外指定了本机已有的 Maven Central 镜像配置和依赖缓存目录；项目自身不依赖这些本机路径。

- 父工程及九个子模块全部构建成功。
- `BootstrapHttpIntegrationTest`：3 个测试，0 失败、0 错误、0 跳过。
- 覆盖系统信息接口、无外部服务时的健康检查、未知路径返回 404 且不包含异常堆栈。
- 仅 `orderpilot-boot/target/orderpilot.jar` 包含 Spring Boot 启动器；其余八个模块为普通依赖 jar。

## 打包程序运行

使用 Java 17 执行打包后的 jar，以本机随机端口验证。第一次启动不设置 `SPRING_PROFILES_ACTIVE`，日志确认自动使用默认 `local`；未提供数据库或模型凭据。

| 请求 | 实际结果 |
| --- | --- |
| `GET /actuator/health` | HTTP 200，`{"status":"UP"}` |
| `GET /api/system/info` | HTTP 200，name=OrderPilot、version=0.1.0-SNAPSHOT、stage=bootstrap |
| `GET /actuator/info` | HTTP 200，应用版本为 0.1.0-SNAPSHOT |
| `GET /missing-bootstrap-route` | HTTP 404，ProblemDetail 响应，不含堆栈或异常类 |

第二次启动显式使用 `mysql`，将数据库地址指向专门保留且没有数据库监听的本机随机端口，使用测试占位凭据。`GET /actuator/health` 实际返回 HTTP 503 / DOWN，确认数据库不可用不会被报为健康。该检查没有访问本机已有 MySQL 实例。

临时启动的验证进程已停止。

## 交付目录复核

已交付至 `C:\Users\admin\Desktop\orderpilot`。在该目录重新执行完整构建，父工程和九个子模块全部成功，3 项集成测试再次通过；随后使用该目录生成的 jar 重复上述默认启动和数据库不可用检查，结果一致。

交付源文件已进行 SHA-256 一致性核对；`git diff --check` 通过，构建产物及本机私密配置由 `.gitignore` 排除。保留原有 LICENSE 与 Git 仓库，没有执行提交或推送。

## 验证边界

- 未验证真实 MySQL 账号及数据库连通性，建库脚本未执行。
- 未连接真实模型，未验证 Agent 任务或业务流程；这些功能尚未实现。
- 当前验证的是后端工程骨架，不代表登录、订单、库存等业务已经可用。
