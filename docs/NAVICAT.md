# 使用 Navicat 连接数据库

这是课设环境，MySQL 直接映射到服务器的 `0.0.0.0:3306`，同学可以从自己的电脑直接用 Navicat 连接。

## 服务器配置

Docker Compose 的 `.env` 使用：

```bash
DB_NAME=library
DB_USER=library
DB_PASSWORD=你的数据库密码
MYSQL_ROOT_PASSWORD=你的MySQL root密码
MYSQL_BIND_ADDRESS=0.0.0.0
MYSQL_PORT=3306
```

启动或更新：

```bash
docker compose up -d --build
```

MySQL 容器会自动创建 `library` 数据库和可以从任意主机登录的 `library` 用户。

## 放行 3306 端口

Ubuntu 服务器执行：

```bash
sudo ufw allow 3306/tcp
```

如果服务器在阿里云、腾讯云、华为云等平台，还需要在云安全组中添加入方向规则：

```text
协议：TCP
端口：3306
来源：0.0.0.0/0
```

## Navicat 连接信息

在 Navicat 新建 MySQL 连接，填这些字段：

| 字段 | 值 |
| --- | --- |
| 连接名 | 图书借阅系统 |
| 主机 | 服务器公网 IP |
| 端口 | `3306` |
| 用户名 | `library` |
| 密码 | `.env` 中的 `DB_PASSWORD` |
| 数据库 | `library` |

第一次启动后，JPA 会自动创建表，并在空表时自动写入 30 本图书、20 位读者、借阅、预约、逾期和催还演示数据。

如果同学连接不上，通常是服务器的云安全组还没有放行 TCP `3306`。
