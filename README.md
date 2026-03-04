<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5-green?style=flat-square&logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk" alt="Java">
  <img src="https://img.shields.io/badge/Vue-3.5-brightgreen?style=flat-square&logo=vuedotjs" alt="Vue">
  <img src="https://img.shields.io/badge/Vite-7-blueviolet?style=flat-square&logo=vite" alt="Vite">
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-7-red?style=flat-square&logo=redis" alt="Redis">
  <img src="https://img.shields.io/badge/license-MIT-blue?style=flat-square" alt="License">
</p>

# FeiTwnd — 个人全栈网站

一套基于 **Spring Boot 3 + Vue 3** 的个人网站全栈解决方案，包含博客、后台管理、个人主页、在线简历四个子站点和一个统一后端服务。

> 在线演示：[blog.feitwnd.cc](https://blog.feitwnd.cc) · [feitwnd.cc](https://feitwnd.cc) · [cv.feitwnd.cc](https://cv.feitwnd.cc)

---

## 功能特性

### 博客端 (Frontend-Blog)
- Markdown 文章渲染（md-editor-v3 预览，与管理端编辑效果一致）
- 文章分类 / 标签 / 归档 / 全文搜索
- 评论系统（嵌套回复、Markdown、悄悄话、邮件通知）
- 留言板（独立于文章的留言系统）
- 友情链接
- 文章点赞
- RSS 订阅（XML Feed + 邮件推送）
- 自动目录提取（TOC）
- 暗黑模式（跟随系统 / 手动切换）
- 响应式设计（移动端适配）
- 访客指纹识别（无需登录即可评论/点赞）

### 管理端 (Frontend-Admin)
- 文章管理（Markdown 编辑器、封面上传、分类/标签管理）
- 评论 / 留言审核管理
- 友情链接管理
- 音乐管理
- 个人信息 / 社交媒体管理
- 访客统计 / 数据看板（ECharts）
- 系统配置（站点设置、关于页面）
- 操作日志

### 个人主页 (Frontend-Home)
- 个人信息展示
- 社交媒体链接
- 简洁大气的单页设计

### 在线简历 (Frontend-Cv)
- 教育 / 工作 / 项目经历展示
- 技能标签
- 响应式布局，适合分享

---

## 🏗 技术架构

```
┌─────────────────────────────────────────────────────────┐
│                      Nginx 反向代理                       │
│  blog.xxx.cc  home.xxx.cc  cv.xxx.cc  admin.xxx.cc      │
└────┬──────────────┬───────────┬──────────────┬──────────┘
     │              │           │              │
     ▼              ▼           ▼              ▼
 Frontend-Blog  Frontend-Home  Frontend-Cv  Frontend-Admin
 (Vue 3+Vite)  (Vue 3+Vite)  (Vue 3+Vite)  (Vue 3+Vite)
     │              │           │              │
     └──────────────┴───────────┴──────────────┘
                        │  /api
                        ▼
              ┌──────────────────┐
              │  Spring Boot 3   │
              │   (Backend)      │
              │   Port: 5922     │
              └───┬─────────┬────┘
                  │         │
          ┌───────┘         └────────┐
          ▼                          ▼
    ┌──────────┐              ┌──────────┐
    │  MySQL 8 │              │ Redis 7  │
    └──────────┘              └──────────┘
          │
    ┌─────┘
    ▼
 Aliyun OSS (图片/文件存储)
```

### 后端技术栈

| 技术 | 说明 |
|---|---|
| Spring Boot 3.5 | 应用框架 |
| Java 21 | 开发语言 |
| MyBatis + PageHelper | ORM + 分页 |
| MySQL 8 + Druid | 数据库 + 连接池 |
| Redis | 缓存 + Session |
| Spring Cache | 缓存抽象 |
| JWT (JJWT 0.12) | 认证授权 |
| Knife4j (OpenAPI 3) | API 文档 |
| Aliyun OSS SDK | 对象存储 |
| WebSocket | 实时通信（在线人数） |
| Bucket4j | 接口限流 |
| CommonMark + Jsoup | Markdown 解析 + HTML 清洗 |
| Thumbnailator + WebP | 图片压缩 |
| Spring Mail | 邮件发送 |

### 前端技术栈

| 技术 | 说明 |
|---|---|
| Vue 3.5 | 前端框架 |
| Vite 7 | 构建工具 |
| Vue Router 4 | 路由 |
| Pinia 3 + Persisted State | 状态管理 + 持久化 |
| Element Plus | UI 组件库 |
| md-editor-v3 | Markdown 编辑/预览 |
| ECharts | 数据可视化（管理端） |
| Axios | HTTP 客户端 |
| Sass | CSS 预处理器 |

---

## 项目结构

```
FeiTwnd/
├── Backend/                    # Spring Boot 后端
│   ├── FeiTwnd-common/         # 公共模块（工具类、常量、异常）
│   ├── FeiTwnd-pojo/           # 实体/DTO/VO
│   └── FeiTwnd-server/         # 主服务（Controller、Service、Mapper）
│       └── src/main/resources/
│           ├── application.yml.template      # 配置模板
│           ├── application-dev.yml.template   # 开发环境模板
│           ├── application-prod.yml.template  # 生产环境模板
│           ├── database/feitwnd.sql           # 数据库建表脚本
│           └── mapper/                        # MyBatis XML
├── Frontend-Blog/              # 博客前端
├── Frontend-Admin/             # 管理后台前端
├── Frontend-Home/              # 个人主页前端
└── Frontend-Cv/                # 在线简历前端
```

---

## 快速开始

### 环境要求

| 环境 | 版本要求 |
|---|---|
| JDK | 21+ |
| Maven | 3.9+ |
| Node.js | 20.19+ 或 22.12+ |
| pnpm | 9+ |
| MySQL | 8.0+ |
| Redis | 7+ |

### 1. 克隆项目

```bash
git clone https://github.com/FeiTwnd/FeiTwnd-Website.git
cd FeiTwnd
```

### 2. 初始化数据库

```bash
# 创建数据库并导入建表脚本
mysql -u root -p -e "CREATE DATABASE FeiTwnd DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p FeiTwnd < Backend/FeiTwnd-server/src/main/resources/database/feitwnd.sql
```

### 3. 配置后端

```bash
cd Backend/FeiTwnd-server/src/main/resources

# 从模板复制配置文件
cp application.yml.template application.yml
cp application-dev.yml.template application-dev.yml

# 编辑 application.yml，填入你的配置：
# - 数据库用户名/密码
# - Redis 密码
# - JWT 密钥
# - 阿里云 OSS 配置
# - QQ 邮箱 + 授权码
# - 网站地址
```

<details>
<summary>需要配置的项目清单</summary>

| 配置项 | 位置 | 说明 |
|---|---|---|
| `spring.datasource.password` | application.yml | MySQL 密码 |
| `spring.redis.password` | application.yml | Redis 密码（无密码则留空） |
| `feitwnd.jwt.secret-key` | application.yml | JWT 签名密钥（随机字符串即可） |
| `feitwnd.alioss.*` | application.yml | 阿里云 OSS 配置 |
| `spring.mail.username` | application.yml | QQ 邮箱地址 |
| `spring.mail.password` | application.yml | QQ 邮箱授权码 |
| `feitwnd.email.personal` | application.yml | 发件人昵称 |
| `feitwnd.email.from` | application.yml | 发件人邮箱 |
| `feitwnd.visitor.verify-code` | application.yml | 访客验证码 |
| `feitwnd.website.*` | application.yml | 网站标题和 4 个子站地址 |
| `feitwnd.jwt.ttl` | application-dev.yml | JWT 过期时间（毫秒） |
| `feitwnd.datasource.*` | application-dev.yml | 数据库连接信息 |
| `feitwnd.redis.*` | application-dev.yml | Redis 连接信息 |

</details>

### 4. 启动后端

```bash
cd Backend
mvn clean package -DskipTests
java -jar FeiTwnd-server/target/FeiTwnd-server-1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

### 5. 启动前端（开发模式）

每个前端项目都可以独立启动：

```bash
# 博客端
cd Frontend-Blog
pnpm install
pnpm dev

# 管理端
cd Frontend-Admin
pnpm install
pnpm dev

# 主页
cd Frontend-Home
pnpm install
pnpm dev

# 简历
cd Frontend-Cv
pnpm install
pnpm dev
```

所有前端默认通过 Vite 代理将 `/api` 请求转发到 `http://localhost:5922`。

### 6. 访问

| 服务 | 地址 |
|---|---|
| 博客端 | http://localhost:5173 |
| 管理端 | http://localhost:5174 |
| 主页 | http://localhost:5175 |
| 简历 | http://localhost:5176 |

> 首次使用需要在数据库管理员账号或访客账号（如果需要），然后在管理端配置个人信息等内容。

---

## 生产部署

### 后端打包

```bash
cd Backend
mvn clean package -DskipTests
# 产出：FeiTwnd-server/target/FeiTwnd-server-1.0-SNAPSHOT.jar

# 使用生产环境配置启动
java -jar FeiTwnd-server-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

### 前端打包

```bash
# 以博客端为例（其余三个同理）
cd Frontend-Blog
pnpm install
pnpm build
# 产出：dist/ 目录，部署到 Nginx 对应站点即可
```

### Nginx 配置参考

```nginx
# 博客端
server {
    listen 80;
    server_name blog.yourdomain.com;
    root /path/to/Frontend-Blog/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:5922/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # WebSocket 支持（在线人数）
    location /api/ws/online {
        proxy_pass http://127.0.0.1:5922/ws/online;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}

# 其他子站（home / admin / cv）配置类似，修改 server_name 和 root 即可
```

---

## 效果预览


| 博客首页 | 文章详情 |
|:---:|:---:|
| ![博客首页](screenshots/blog-home.png) | ![文章详情](screenshots/blog-article.png) |

| 管理后台 | 暗黑模式 |
|:---:|:---:|
| ![管理后台](screenshots/admin-dashboard.png) | ![暗黑模式](screenshots/blog-dark.png) |


---

## 贡献

欢迎提交 Issue 和 Pull Request！详见 [CONTRIBUTING.md](CONTRIBUTING.md)。

## 开源协议

本项目采用 [MIT](LICENSE) 协议开源。

## Star History

如果这个项目对你有帮助，欢迎 Star 支持一下！
