# FeiTwnd Docker 部署指南

> **重要提醒**: 文档中的域名 `example.com`、`admin.example.com` 等仅为示例，请根据实际情况替换为你自己的域名。

本项目支持使用 Docker 进行快速部署，包含后端 Java 应用、MySQL、Redis 和 Nginx 反向代理。

## 目录结构

```
FeiTwnd/
├── Dockerfile              # 后端应用镜像
├── docker-compose.yml     # 服务编排
└── docker/
    ├── .env.example       # 环境变量示例 (复制为 .env 后修改)
    ├── nginx/
    │   ├── nginx.conf          # Nginx 主配置
    │   ├── conf.d/
    │   │   └── feitwnd-backend.conf  # 后端代理配置
    │   └── sites-enabled/
    │       └── feitwnd.cc      # 前端站点配置（使用自己的域名时需重命名该文件并修改 server_name/root）
    ├── mysql/
    │   └── init/           # MySQL 初始化脚本
    └── html/               # 前端静态文件目录
```

## 前置要求

- Docker >= 20.10
- Docker Compose >= 2.0
- 域名已解析到服务器（用于 HTTPS）

## 部署步骤

### 1. 配置环境变量

```bash
# 注意：.env 必须放在仓库根目录（与 docker-compose.yml 同级），docker compose 只读取该位置的 .env
cp docker/.env.example .env
```

编辑 `.env` 文件，**必须修改以下配置**：

```bash
# ============================================
# MySQL配置 (必须修改)
# ============================================
MYSQL_ROOT_PASSWORD=your_root_password_here
MYSQL_PASSWORD=your_password_here

# ============================================
# JWT密钥 (必须修改!)
# ============================================
# 使用随机字符串生成，至少32位
# 可以使用: openssl rand -base64 32
JWT_SECRET_KEY=your-very-long-random-secret-key-here-change-this

# ============================================
# 阿里云OSS配置 (必须修改)
# ============================================
ALIOSS_ENDPOINT=oss-cn-shanghai.aliyuncs.com
ALIOSS_ACCESS_KEY_ID=your-access-key-id
ALIOSS_ACCESS_KEY_SECRET=your-access-key-secret
ALIOSS_BUCKET_NAME=your-bucket-name

# ============================================
# 邮件配置 (必须修改)
# ============================================
EMAIL_PERSONAL=YourName
EMAIL_FROM=your-email@example.com

# ============================================
# 访客登录验证码 (必须修改)
# ============================================
VISITOR_VERIFY_CODE=123456

# ============================================
# 网站URL配置 (替换为你的实际域名)
# ============================================
WEBSITE_TITLE=YourSiteTitle
WEBSITE_HOME=https://example.com
WEBSITE_ADMIN=https://admin.example.com
WEBSITE_CV=https://cv.example.com
WEBSITE_BLOG=https://blog.example.com
```

### 2. 初始化数据库

**无需手动导入**：MySQL 容器首次启动时会自动执行 `docker/mysql/init/feitwnd.sql`（挂载到 `/docker-entrypoint-initdb.d`），自动创建 `FeiTwnd` 数据库、`feitwnd` 用户及全部数据表。

> 已包含：管理员账号、游客账号、系统配置等初始数据。初始 SQL 中管理员/游客账号为 `xxx` 占位符，**登录前需先修改数据库**：用户名改为你自己的，密码需用后端测试类 `FeiTwndBackendApplicationTests.testPassword` 生成加密值（SHA-256(password+salt)）后更新（详见根目录 README"快速开始"章节）。

### 3. 放入前端静态文件

构建前端项目后，将静态文件放入对应目录：

```bash
# 主站
mkdir -p docker/html/example.com/html
cp -r ../Frontend-Home/dist/* docker/html/example.com/html/

# 博客
mkdir -p docker/html/blog.example.com/html
cp -r ../Frontend-Blog/dist/* docker/html/blog.example.com/html/

# 简历
mkdir -p docker/html/cv.example.com/html
cp -r ../Frontend-Cv/dist/* docker/html/cv.example.com/html/

# 管理后台
mkdir -p docker/html/admin.example.com/html
cp -r ../Frontend-Admin/dist/* docker/html/admin.example.com/html/
```

> 目录名中的域名（如 `blog.example.com`）必须与 `docker/nginx/sites-enabled/` 配置里的 `server_name` 和 `root` 保持一致（默认配置为 `feitwnd.cc` 系列域名，使用自己的域名时请统一修改）。

### 4. 构建并启动服务

```bash
# 构建并启动所有服务
docker compose up -d --build

# 查看运行状态
docker compose ps

# 查看日志
docker compose logs -f
```

### 5. 启用 AI 摘要模块（可选）

默认镜像不包含 AI 模块（包体最小）。如需启用，在 `.env` 中设置：

```bash
AI_ENABLED=true            # 以 -Pwith-ai 构建镜像（包含 langchain4j）
AI_BASE_URL=https://api.deepseek.com/v1   # OpenAI 兼容协议接口地址
AI_API_KEY=your-api-key    # 模型 API 秘钥
AI_MODEL_NAME=deepseek-chat
```

然后重新构建启动：

```bash
docker compose up -d --build backend
```

启用后管理端文章编辑页会出现"AI 生成摘要"开关（详见根目录 README 的"AI 摘要模块"章节）。

### 6. 配置 HTTPS（可选）

#### 使用 Certbot 自动配置

```bash
# 进入 Nginx 容器
docker exec -it feitwnd-nginx sh

# 安装 Certbot
apk add certbot python3

# 申请证书（需要域名已解析）
certbot certonly --webroot -w /var/www/example.com/html -d example.com
certbot certonly --webroot -w /var/www/blog.example.com/html -d blog.example.com
certbot certonly --webroot -w /var/www/cv.example.com/html -d cv.example.com
certbot certonly --webroot -w /var/www/admin.example.com/html -d admin.example.com
```

#### 手动配置 HTTPS

编辑 `docker/nginx/sites-enabled/example.com`，添加 SSL 配置：

```nginx
server {
    listen 443 ssl http2;
    server_name example.com;

    ssl_certificate /etc/letsencrypt/live/example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/example.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;

    # ... 其他配置
}

# HTTP 重定向到 HTTPS
server {
    listen 80;
    server_name example.com;
    return 301 https://$server_name$request_uri;
}
```

## 环境变量说明

| 变量名 | 必填 | 说明 | 默认值 |
|--------|------|------|--------|
| `MYSQL_ROOT_PASSWORD` | 是 | MySQL root 密码 | - |
| `MYSQL_PASSWORD` | 是 | MySQL feitwnd 用户密码 | - |
| `REDIS_PASSWORD` | 否 | Redis 密码（无需密码则留空） | - |
| `JWT_SECRET_KEY` | 是 | JWT 签名密钥，建议至少32位随机字符串 | - |
| `JWT_TTL` | 否 | JWT 过期时间(毫秒) | 86400000 (24小时) |
| `ALIOSS_ENDPOINT` | 是 | 阿里云 OSS 地域节点 | - |
| `ALIOSS_ACCESS_KEY_ID` | 是 | 阿里云 AccessKey ID | - |
| `ALIOSS_ACCESS_KEY_SECRET` | 是 | 阿里云 AccessKey Secret | - |
| `ALIOSS_BUCKET_NAME` | 是 | 阿里云 OSS Bucket 名称 | - |
| `EMAIL_PERSONAL` | 是 | 邮件发送者昵称 | - |
| `EMAIL_FROM` | 是 | 邮件发送者邮箱 | - |
| `VISITOR_VERIFY_CODE` | 是 | 访客登录验证码 | - |
| `WEBSITE_TITLE` | 否 | 网站标题 | - |
| `WEBSITE_HOME` | 否 | 首页地址 | - |
| `WEBSITE_ADMIN` | 否 | 管理后台地址 | - |
| `WEBSITE_CV` | 否 | 简历地址 | - |
| `WEBSITE_BLOG` | 否 | 博客地址 | - |
| `AI_ENABLED` | 否 | 是否构建并启用 AI 摘要模块 | false |
| `AI_BASE_URL` | 否 | 模型接口地址（OpenAI 兼容协议） | https://api.deepseek.com/v1 |
| `AI_API_KEY` | 否 | 模型 API 秘钥 | - |
| `AI_MODEL_NAME` | 否 | 模型名称 | deepseek-chat |

## 常用命令

```bash
# 启动服务
docker compose up -d

# 停止服务
docker compose down

# 重启服务
docker compose restart

# 查看日志
docker compose logs -f backend    # 后端日志
docker compose logs -f nginx       # Nginx 日志
docker compose logs -f mysql       # MySQL 日志
docker compose logs -f redis       # Redis 日志

# 进入容器
docker exec -it feitwnd-backend sh
docker exec -it feitwnd-mysql mysql -uroot -p
docker exec -it feitwnd-redis redis-cli

# 重新构建后端
docker compose build backend
docker compose up -d backend
```

## 数据持久化

- MySQL 数据: `mysql_data` 卷
- Redis 数据: `redis_data` 卷
- Nginx 日志: `nginx_logs` 卷
- 前端静态文件: `docker/html/` 目录
- 后端日志: `docker/app/logs/` 目录

## 注意事项

1. **数据库初始化**: MySQL 容器首次启动自动执行 `docker/mysql/init/feitwnd.sql`，无需手动导入；若需重置数据，删除 `mysql_data` 卷后重新启动
2. **敏感信息**: `.env` 文件包含敏感信息，请勿提交到版本控制（已加入 .gitignore）
3. **内存配置**: Dockerfile 中 JVM 堆内存设置为 `-Xmx1024m -Xms256m`（4GB 内存机器同时运行 MySQL/Redis/Nginx），可根据服务器配置调整
4. **安全建议**:
   - 修改默认端口（5922）
   - 使用强密码
   - 配置防火墙规则
5. **前端 API 地址**: 确保前端构建时配置的 API 地址指向正确的后端地址
6. **AI 模块**: 默认镜像不含 AI 模块，启用方式见上文第 5 步；秘钥仅存于 `.env`，不会写入镜像

## 故障排查

### 后端启动失败

```bash
# 查看后端日志
docker compose logs backend

# 检查数据库连接
docker exec -it feitwnd-backend sh
# 在容器内: telnet mysql 3306
```

### Nginx 502 错误

```bash
# 检查后端是否运行
docker compose ps

# 检查 Nginx 配置
docker exec feitwnd-nginx nginx -t
```

### 数据库连接问题

```bash
# 检查 MySQL 是否就绪
docker compose ps

# 查看 MySQL 日志
docker compose logs mysql
```
