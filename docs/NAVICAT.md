# 使用 Navicat 连接数据库

本项目默认的 `server` 配置使用文件型 H2，`mysql` 配置才适合直接用 Navicat 查看。

## 推荐：切换到 MySQL

在服务器上准备一个 MySQL 8 数据库，并把下面的环境变量写进部署配置：

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

如果使用 systemd，就把 `library-borrowing.service` 中的启动参数改成 `--spring.profiles.active=mysql`，并确保 `EnvironmentFile` 里包含上面的 MySQL 变量。

## Navicat 连接信息

在 Navicat 新建 MySQL 连接，填这些字段：

| 字段 | 建议值 |
| --- | --- |
| 连接名 | 图书借阅系统 |
| 主机 | 服务器 IP 或 `127.0.0.1` |
| 端口 | `3306` |
| 用户名 | `library` |
| 密码 | 你的 `DB_PASSWORD` |
| 数据库 | `library` |

第一次启动后，JPA 会自动创建表，并在空表时自动写入 30 本图书、20 位读者、借阅、预约、逾期和催还演示数据。

## 如果服务器还在用 H2

当前部署模板中的 `--spring.profiles.active=server` 使用的是文件型 H2，不能像 MySQL 一样用 Navicat 的 MySQL 连接直接打开。可以选择：

1. 切换到上面的 `mysql` 配置。
2. 或使用支持 H2/JDBC 的数据库工具连接 H2 文件数据库。

具体服务器 IP、MySQL 账号和密码需要向负责部署服务器的人确认。
