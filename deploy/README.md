# Docker 服务器部署

服务器使用 Docker Compose 部署，包含两个容器：

- `mysql`：MySQL 8.0，数据保存在 Docker 命名卷 `mysql-data`
- `app`：图书借阅系统，监听容器内 `18080`

宿主机 Nginx 继续反向代理到 `127.0.0.1:18080`。

## 1. 安装 Docker

Ubuntu 服务器安装 Docker Engine 和 Compose 插件后，确认命令可用：

```bash
docker --version
docker compose version
```

## 2. 配置环境变量

进入项目根目录，从模板创建正式配置：

```bash
cp .env.example .env
```

至少修改这些密码：

```bash
DB_PASSWORD=你的数据库密码
MYSQL_ROOT_PASSWORD=你的MySQL root密码
ADMIN_PASSWORD=管理员初始密码
READER_PASSWORD=读者初始密码
```

默认配置会让 MySQL 只监听服务器的 `127.0.0.1:3306`，不直接暴露到公网。

## 3. 构建并启动

```bash
docker compose up -d --build
docker compose ps
docker compose logs -f app
```

第一次启动时，MySQL 会自动创建 `library` 数据库和 `library` 用户，JPA 会自动建表并写入演示数据。

## 4. 配置 Nginx

把 `deploy/nginx-library.conf` 加入服务器现有 Nginx 配置，访问路径为：

```text
https://你的域名/library/
```

## 5. 更新项目

```bash
git pull
docker compose up -d --build
```

## 6. Navicat

推荐在 Navicat 的 SSH 标签页配置服务器 SSH，然后 MySQL 连接使用：

```text
主机：127.0.0.1
端口：3306
数据库：library
用户名：library
密码：DB_PASSWORD
```

如果确实需要直接连接服务器 3306，可以把 `.env` 中的 `MYSQL_BIND_ADDRESS` 改为 `0.0.0.0`，然后重启 Compose，并在云安全组或防火墙中只放行你的电脑 IP。完整说明见 `docs/NAVICAT.md`。

## 常用命令

```bash
docker compose stop
docker compose start
docker compose down
docker compose logs -f mysql
```

`docker compose down` 不会删除数据库卷；只有显式执行 `docker compose down -v` 才会删除数据。
