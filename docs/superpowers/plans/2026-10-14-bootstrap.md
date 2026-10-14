# OrderPilot Bootstrap Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** 在用户指定的已克隆仓库中建立可编译、可启动的 Maven 父子模块骨架。

**Architecture:** 八个业务 jar 与一个 boot 启动模块，所有业务包位于 com.orderpilot。默认 local 启动不依赖数据库或模型凭据，mysql profile 显式启用数据源。

**Tech Stack:** Java 17、Maven 3.9.8、Spring Boot 3.5.13、Spring AI 1.1.8、MySQL Connector/J。

**Spec:** ../../architecture.md（本次已获用户授权的框架范围与业务约定）

## Global Constraints

- 在用户指定目录交付，不改动其他项目或仓库远程配置，不自动提交或推送。
- 业务范围：单企业、多店铺、单仓、已支付订单接入与整单履约。
- 框架范围不包含业务 CRUD、数据库表、前端应用或真实模型接入。
- 无生产账号、密码或模型密钥；默认仅监听本机。

## Task 1: Maven 工程及启动配置

Files: pom.xml；orderpilot-{system,channel,product,oms,inventory,wms,workorder,agent,boot}/pom.xml；orderpilot-boot/src/main/java/com/orderpilot/OrderPilotApplication.java；orderpilot-boot/src/main/resources/application*.yml。

- [x] 建立版本统一的父 POM 和九个子模块，仅 boot 使用 Spring Boot repackage。
- [x] 在业务模块建立 package-info，避免伪造业务实现；agent 仅引入 ChatClient 库。
- [x] 创建默认 local 与显式 mysql 配置，不自动运行 SQL。
- [x] 使用 `mvn -B -ntp test` 验证 reactor 构建。

## Task 2: HTTP 框架验证

Files: orderpilot-system/src/main/java/com/orderpilot/system/controller/SystemInfoController.java；同模块 SystemInfo record；orderpilot-boot/src/test/java 下的集成测试。

Produces: GET /api/system/info 返回 name=OrderPilot、version=0.1.0-SNAPSHOT、stage=bootstrap。健康检查使用 Actuator。

- [x] 先编写真实 HTTP 集成测试：启动不要求数据库或模型密钥，健康检查 UP，系统接口字段正确，未知路径 404 不泄露堆栈。
- [x] 在 system 接口实现前运行测试，确认缺失接口导致预期失败。
- [x] 实现最小 Controller 与 immutable record，再运行测试验证通过。

## Task 3: 使用说明与交付验证

Files: README.md；mvnw、mvnw.cmd、.mvn/wrapper；docs/database/00-create-database.sql；requests/orderpilot.http；.gitignore；.editorconfig；docs/verification.md。

- [x] 生成官方 Maven Wrapper，固定 Maven 3.9.8，说明 Java 17、IDEA 导入、package 和 jar 启动命令。
- [x] 写明 local/mysql 区别、环境变量、手工建库方式与尚未实现的功能。
- [x] 完整运行 `mvn clean verify`，检查仅 boot 生成可执行 jar。
- [x] 启动打包后的 jar，实际请求 health、system/info、未知路径并记录结果，完成后停止临时进程。
- [x] 检查 Git diff、凭据与构建产物忽略规则；交付用户指定目录并核对文件一致。

## 执行约定

先在授权工作区暂存和验证，再通过受控文件写入交付到用户指定的桌面仓库。用户已要求直接搭框架，因此不再重复询问设计或执行许可。
