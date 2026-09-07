# 图书借阅管理系统

面向课程实训的软件度量项目，后端按四个业务模块组织：

- A：系统基座、登录权限和操作日志
- B：图书、书架位置、读者类型与借阅规则
- C：借书、还书、续借、预约与催还流程
- D：借阅资格、逾期罚金和流通统计

## 本地运行

当前基线兼容 Java 8，使用 Spring Boot 2.7.18 和内存 H2 数据库。

```bash
mvn spring-boot:run
```

浏览器访问 `http://localhost:8080/`。演示账号：管理员 `admin / admin123`，读者 `reader / reader123`。

运行测试：`mvn test`。

接口文档：`http://localhost:8080/swagger-ui/index.html`。

## 已实现功能

- 登录认证、管理员和读者角色控制、账号启停与操作日志
- 图书检索、新增、编辑、删除及库存联动
- 读者资料、读者类型、借期、续借次数和罚金规则
- 借书、还书、续借、预约、取消预约和逾期清单
- 逾期罚金计算、借阅资格判断及流通统计
- 管理员后台和读者端响应式网页
- H2 本地/服务器持久化配置及 MySQL 可选配置

## 配置环境

- 默认：内存 H2，适合开发和测试
- `server`：文件型 H2，适合课程演示服务器
- `mysql`：MySQL 8，通过 `DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USER`、`DB_PASSWORD` 配置

```bash
java -jar target/library-borrowing-system-0.1.0-SNAPSHOT.jar --spring.profiles.active=server
```

## 分支

- `main`：稳定版本
- `dev`：日常集成
- `feature/A-auth`：权限与日志
- `feature/B-data`：基础数据
- `feature/C-circulation`：借阅流程
- `feature/D-analysis`：计算统计
