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

课设环境默认让 MySQL 监听服务器的 `0.0.0.0:3306`，方便同学用 Navicat 直接连接。

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

同学直接新建 MySQL 连接即可：

```text
主机：服务器公网 IP
端口：3306
数据库：library
用户名：library
密码：DB_PASSWORD
```

服务器防火墙和云安全组需要放行 TCP `3306`。如果同学连接不上，优先检查这两个地方。

## 常用命令

```bash
docker compose stop
docker compose start
docker compose down
docker compose logs -f mysql
```

`docker compose down` 不会删除数据库卷；只有显式执行 `docker compose down -v` 才会删除数据。
