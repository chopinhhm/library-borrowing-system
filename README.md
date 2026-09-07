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

演示账号：管理员 `admin / admin123`，读者 `reader / reader123`。

运行测试：`mvn test`。

## 分支

- `main`：稳定版本
- `dev`：日常集成
- `feature/A-auth`：权限与日志
- `feature/B-data`：基础数据
- `feature/C-circulation`：借阅流程
- `feature/D-analysis`：计算统计
