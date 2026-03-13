# FeiTwnd Docker 部署指南

本项目支持使用 Docker 进行快速部署，包含后端 Java 应用、MySQL、Redis 和 Nginx 反向代理。

## 目录结构

```
FeiTwnd/
├── Dockerfile              # 后端应用镜像
├── docker-compose.yml     # 服务编排
└── docker/
    ├── .env.example       # 环境变量示例
    ├── nginx/
    │   ├── nginx.conf          # Nginx 主配置
    │   ├── conf.d/
    │   │   └── feitwnd-backend.conf  # 后端代理配置
    │   └── sites-enabled/
    │       └── feitwnd.cc      # 前端站点配置
    ├── mysql/
    │   └── init/           # MySQL 初始化脚本
    └── html/               # 前端静态文件目录
```

## 前置要求

- Docker >= 20.10
- Docker Compose >= 2.0
- 域名已解析到服务器（用于 HTTPS）

## 快速开始

### 1. 复制环境变量配置

```bash
cd docker
cp .env.example .env
# 编辑 .env 文件，修改数据库密码
```

### 2. 放入前端静态文件

构建前端项目后，将静态文件放入对应目录：

```bash
# 主站
mkdir -p html/feitwnd.cc/html
cp -r ../Frontend-Home/dist/* html/feitwnd.cc/html/

# 博客
mkdir -p html/blog.feitwnd.cc/html
cp -r ../Frontend-Blog/dist/* html/blog.feitwnd.cc/html/

# 简历
mkdir -p html/cv.feitwnd.cc/html
cp -r ../Frontend-Cv/dist/* html/cv.feitwnd.cc/html/

# 管理后台
mkdir -p html/admin.feitwnd.cc/html
cp -r ../Frontend-Admin/dist/* html/admin.feitwnd.cc/html/
```

### 3. 启动服务

```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看运行状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 4. 配置 HTTPS（可选）

#### 使用 Certbot 自动配置

```bash
# 进入 Nginx 容器
docker exec -it feitwnd-nginx sh

# 安装 Certbot
apk add certbot python3

# 申请证书（需要域名已解析）
certbot certonly --webroot -w /var/www/feitwnd.cc/html -d feitwnd.cc
certbot certonly --webroot -w /var/www/blog.feitwnd.cc/html -d blog.feitwnd.cc
certbot certonly --webroot -w /var/www/cv.feitwnd.cc/html -d cv.feitwnd.cc
certbot certonly --webroot -w /var/www/admin.feitwnd.cc/html -d admin.feitwnd.cc
```

#### 手动配置 HTTPS

编辑 `docker/nginx/sites-enabled/feitwnd.cc`，添加 SSL 配置：

```nginx
server {
    listen 443 ssl http2;
    server_name feitwnd.cc;

    ssl_certificate /etc/letsencrypt/live/feitwnd.cc/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/feitwnd.cc/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;

    # ... 其他配置
}

# HTTP 重定向到 HTTPS
server {
    listen 80;
    server_name feitwnd.cc;
    return 301 https://$server_name$request_uri;
}
```

## 常用命令

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f backend    # 后端日志
docker-compose logs -f nginx       # Nginx 日志
docker-compose logs -f mysql       # MySQL 日志
docker-compose logs -f redis       # Redis 日志

# 进入容器
docker exec -it feitwnd-backend sh
docker exec -it feitwnd-mysql mysql -uroot -p
docker exec -it feitwnd-redis redis-cli

# 重新构建后端
docker-compose build backend
docker-compose up -d backend
```

## 数据持久化

- MySQL 数据: `mysql_data` 卷
- Redis 数据: `redis_data` 卷
- Nginx 日志: `nginx_logs` 卷
- 前端静态文件: `docker/html/` 目录
- 后端日志: `docker/app/logs/` 目录

## 注意事项

1. **首次部署**: 需要在 MySQL 容器中导入数据库初始化脚本（如果有的话）
2. **内存配置**: Dockerfile 中 JVM 堆内存设置为 `-Xmx2048m -Xms512m`，可根据服务器配置调整
3. **安全建议**:
   - 修改默认端口（5922）
   - 使用强密码
   - 配置防火墙规则
4. **前端 API 地址**: 确保前端配置的 API 地址正确（在构建前端时配置 VITE_API_BASE_URL）

## 故障排查

### 后端启动失败

```bash
# 查看后端日志
docker-compose logs backend

# 检查数据库连接
docker exec -it feitwnd-backend sh
# 在容器内: telnet mysql 3306
```

### Nginx 502 错误

```bash
# 检查后端是否运行
docker-compose ps

# 检查 Nginx 配置
docker exec feitwnd-nginx nginx -t
```

### 数据库连接问题

```bash
# 检查 MySQL 是否就绪
docker-compose ps

# 查看 MySQL 日志
docker-compose logs mysql
```
