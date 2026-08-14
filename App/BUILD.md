# FeiTwnd-App 打包说明（Android APK · EAS Build）

Expo（React Native）编写的移动端管理后台，与 `Frontend-Admin` 共用后端接口。
打 APK 走 **EAS 云端构建**，本机不需要安装 Android Studio / SDK / JDK。

---

## 一、前置条件

- Node.js（建议 LTS 版本）
- pnpm：`npm i -g pnpm`
- Expo 账号（https://expo.dev 免费注册）
- 一个 EAS 项目（首次打包时创建，见"首次配置"）

## 二、首次配置（只需做一次）

### 1. 安装依赖

```bash
cd App
pnpm install
```

### 2. 关联你的 EAS 项目

仓库本身不包含任何人的 EAS 账号信息（projectId / owner 都不入库），需要你在本地创建一个 `App/.local.eas.json`：

```bash
cp .local.eas.example.json .local.eas.json
```

编辑 `.local.eas.json`，填入你自己的 EAS 项目信息：

```json
{
  "projectId": "你的项目 ID",
  "owner": "你的 Expo 用户名"
}
```

- 该文件已被 .gitignore 忽略，不会提交进仓库。
- 还不知道 projectId？先执行第 4 步的 `npx eas-cli init`，它会创建/关联项目并打印 projectId，再回来填。

### 3. 登录 Expo 并初始化 EAS 项目（仅首次）

```bash
npx eas-cli login
npx eas-cli init
```

- `eas init` 会在 EAS 服务端创建/关联项目，并把 projectId 打印在终端里，填进第 2 步的 `.local.eas.json`。
- 本仓库使用动态配置（`app.config.js`），`eas init` 不会自动写入 projectId，需要手动填写。
- 之后同一仓库再打包可跳过本步。

### 4. 配置服务器地址（EAS 云端构建）

> EAS 云端构建**不读取本地 `.env`**，环境变量必须通过 `eas env:set` 存在 EAS 服务端（同样不进仓库）。

```bash
npx eas-cli env:set EXPO_PUBLIC_API_URL https://你的后端地址
```

按提示选择：可见性选 **Plain text**，环境范围按默认（全部）即可。

> 本地用 Expo Go / `expo start` 调试时才需要项目根目录的 `.env`（不影响云端打包）：
>
> ```bash
> cp .env.example .env
> # 编辑 .env 的 EXPO_PUBLIC_API_URL
> ```

### 5. 替换应用图标（可选）

准备一张 **1024×1024 的 PNG**，覆盖 `assets/images/icon.png`：

- logo 放在画面中央约 2/3 范围内（系统自适应遮罩会裁掉边缘，贴边会被切掉）。
- 建议纯色/圆角背景、无透明边框。
- Expo 会自动基于 `icon.png` 生成 Android 自适应图标（背景色 `#f5f7fa`）。

## 三、打包 APK

```bash
npx eas-cli build -p android --profile preview
```

- `eas.json` 中 `preview` 已配置为 `distribution: internal` + `buildType: apk`，产出**可直接安装的 APK**（而非上架 Google Play 的 AAB）。
- 签名由 EAS 自动管理；同一项目后续更新复用同一 keystore，可覆盖安装旧包。
- 构建完成后终端会给出下载链接，手机浏览器打开下载安装即可。
- 或用数据线连接手机：`adb install app-release.apk`。

## 四、更新发布

1. 修改代码后重新构建：`npx eas-cli build -p android --profile preview`。
2. 若新包覆盖安装旧包失败，递增 `App/app.json` 里 `expo.version`（或补 `android.versionCode`），保持版本号上升。
3. 改了服务器地址：重新执行第 4 步的 `eas env:set`，再重新打包。

## 五、常见问题

| 现象 | 处理 |
|---|---|
| App 打开就闪退、登录页都看不到 | 基本是 `EXPO_PUBLIC_API_URL` 没注入构建：确认已执行第 4 步 `eas env:set` |
| 登录页提示"服务器地址未配置" | 同上，env 为空。云端打包必须走 `eas env:set`，本地 `.env` 对 EAS 无效 |
| `eas build` 提示需要配置项目 | 检查 `.local.eas.json` 是否已创建并填好 projectId（见第 2 步） |
| 构建提示 expo-notifications 需要 FCM / google-services.json | 本 App 只使用**本地通知**，不依赖 FCM。检查 `app.json` 是否误加了 `googleServicesFile`，移除即可；仅做远程推送才需要 Firebase |
| APK 装到手机后连不上服务器 | 确认 `eas env:set` 的地址在手机当前网络下可访问（局域网 IP 换网后会失效）；release 包默认拦截明文 `http://` |
| 改了 `app.json` / 图标 / `eas.json` 没生效 | EAS 每次构建都会重新生成原生工程，重新构建即可 |
