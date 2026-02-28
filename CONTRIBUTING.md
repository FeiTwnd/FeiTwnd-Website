# 贡献指南

感谢你对 FeiTwnd 项目的关注！欢迎通过以下方式参与贡献。

## 报告 Bug

1. 在 [Issues](../../issues) 页面搜索是否已有相同问题
2. 如果没有，请创建新 Issue 并包含以下信息：
   - 问题描述
   - 复现步骤
   - 期望行为 vs 实际行为
   - 运行环境（浏览器、操作系统、Node.js 版本等）
   - 截图或错误日志（如有）

## 功能建议

欢迎在 Issues 中提出功能建议，请尽量描述清楚：
- 你想要什么功能
- 为什么需要这个功能
- 可能的实现思路（可选）

## 提交 Pull Request

### 开发环境准备

```bash
# 1. Fork 本仓库并克隆到本地
git clone https://github.com/FeiTwnd/FeiTwnd-Website.git
cd FeiTwnd-Website

# 2. 创建新分支
git checkout -b feat/your-feature-name
# 或
git checkout -b fix/your-bug-fix

# 3. 安装依赖并启动开发（参考 README.md 快速开始章节）
```

### 提交规范

请使用以下格式的 Commit Message：

```
<type>(<scope>): <description>

[可选的详细说明]
```

**type 类型：**

| 类型 | 说明 |
|---|---|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `docs` | 文档更新 |
| `style` | 代码格式（不影响逻辑） |
| `refactor` | 重构 |
| `perf` | 性能优化 |
| `test` | 测试相关 |
| `chore` | 构建/依赖/配置变更 |

**scope 范围（可选）：**
- `blog` — 博客前端
- `admin` — 管理端前端
- `home` — 个人主页前端
- `cv` — 简历前端
- `backend` — 后端服务
- `db` — 数据库相关

**示例：**
```
feat(blog): 添加文章搜索高亮功能
fix(backend): 修复评论审核状态更新异常
docs: 更新部署文档
```

### PR 要求

1. 确保代码能正常构建（`mvn clean package -DskipTests` / `pnpm build`）
2. 前端代码需通过 ESLint 检查（`pnpm lint`）
3. 一个 PR 只做一件事，保持改动范围清晰
4. 如果涉及数据库变更，请在 PR 描述中说明 SQL 变更内容
5. 不要提交包含敏感信息的配置文件

## 代码规范

### 后端 (Java)
- 遵循阿里巴巴 Java 开发规约
- 使用 Lombok 简化代码
- Service 层需写清楚方法注释
- Controller 入参使用 DTO，返回使用 VO

### 前端 (Vue)
- 使用 Vue 3 `<script setup>` 语法
- 组件使用 PascalCase 命名
- CSS 使用 scoped + CSS 变量适配暗黑模式
- 提交前执行 `pnpm lint` 确保代码风格一致

## 协议

参与贡献即表示你同意你的代码以 [MIT](LICENSE) 协议开源。
