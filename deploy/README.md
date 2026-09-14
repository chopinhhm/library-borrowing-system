# 服务器部署

服务器现在使用 MySQL 配置启动，不再使用文件型 H2。

## 0. 在同台服务器安装 MySQL

Ubuntu 服务器可以执行：

```bash
sudo apt update
sudo apt install -y mysql-server
sudo systemctl enable --now mysql
```

应用仍通过 `DB_HOST=127.0.0.1` 连接本机 MySQL。

## 1. 初始化 MySQL

使用 MySQL 管理员账号执行 `deploy/mysql-init.sql`，并将脚本中的占位密码替换为正式密码。

## 2. 配置环境变量

把 `deploy/library.env.example` 放到服务器：

```text
/opt/library-borrowing/library.env
```

至少确认这些值：

```bash
PORT=18080
SERVER_ADDRESS=127.0.0.1
CONTEXT_PATH=/library
DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=library
DB_USER=library
DB_PASSWORD=你的数据库密码
ADMIN_PASSWORD=管理员初始密码
READER_PASSWORD=读者初始密码
```

## 3. 安装 systemd 服务

把 `deploy/library-borrowing.service` 放到：

```text
/etc/systemd/system/library-borrowing.service
```

然后重新加载并启动服务。

## 4. 配置 Nginx

把 `deploy/nginx-library.conf` 的内容加入服务器现有 Nginx 站点配置，访问路径为：

```text
https://你的域名/library/
```

## 5. 确认数据库

服务第一次启动后，JPA 会自动建表。空数据库会自动写入 30 本图书、20 位读者和演示借阅数据。

Navicat 使用 MySQL 类型连接：

```text
主机：服务器 IP
端口：3306
数据库：library
用户名：library
密码：DB_PASSWORD
```

Navicat 远程连接推荐使用 SSH 隧道，这样 MySQL 可以继续只监听 `127.0.0.1`，不用把 3306 暴露给整个公网。若必须直接连接，需要让 MySQL 监听服务器网卡，并在云安全组或防火墙中只放行你的电脑 IP。完整说明见 `docs/NAVICAT.md`。
