# 使用 Navicat 连接数据库

服务器部署现已默认使用 MySQL，MySQL 和项目部署在同一台服务器上。应用通过 `127.0.0.1:3306` 连接数据库，不需要把 MySQL 端口暴露到公网。

## 服务器上的 MySQL 配置

部署环境变量应包含：

```bash
DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=library
DB_USER=library
DB_PASSWORD=你的数据库密码
```

服务启动参数使用 `mysql` 配置：

```bash
java -jar library.jar --spring.profiles.active=mysql
```

使用 MySQL 管理员账号执行 `deploy/mysql-init.sql` 可以创建数据库和 `library` 用户。

## Navicat 连接信息

在 Navicat 新建 MySQL 连接，填这些字段：

| 字段 | 建议值 |
| --- | --- |
| 连接名 | 图书借阅系统 |
| 主机 | 服务器公网 IP |
| 端口 | `3306` |
| 用户名 | `library` |
| 密码 | 你的 `DB_PASSWORD` |
| 数据库 | `library` |

第一次启动后，JPA 会自动创建表，并在空表时自动写入 30 本图书、20 位读者、借阅、预约、逾期和催还演示数据。

## 推荐：使用 SSH 隧道

在 Navicat 连接设置的 SSH 标签页填写：

| 字段 | 值 |
| --- | --- |
| 主机 | 服务器公网 IP |
| 端口 | `22` |
| 用户名 | 服务器 SSH 用户，例如 `ubuntu` |
| 认证方式 | 密码或 SSH 私钥 |

MySQL 标签页继续填写：

```text
主机：127.0.0.1
端口：3306
用户名：library
密码：DB_PASSWORD
数据库：library
```

这种方式下 MySQL 可以继续只监听 `127.0.0.1`，不用向公网开放 3306。

如果选择直接连接，则需要让 MySQL 监听服务器网卡，并在云安全组或防火墙中只放行你的电脑 IP。为了安全，不建议对所有 IP 开放 MySQL 端口。
