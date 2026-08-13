# FeiTwnd-App 打包说明（Android APK · EAS Build）

本 App 是 Expo（React Native）编写的移动端管理后台，与 `Frontend-Admin` 共用后端接口。

拉取代码后，只需两步即可打出**可直接安装的 APK**：改 `.env` → EAS 云端构建。
不需要本机安装 Android Studio / SDK / JDK。

---

## 一、前置条件

- Node.js（建议 LTS 版本）
- pnpm：`npm i -g pnpm`
- 一个 Expo 账号（https://expo.dev 免费注册，登录一次即可）

## 二、拉取后配置

### 1. 安装依赖

```bash
cd App
pnpm install
```

### 2. 配置服务器地址

```bash
cp .env.example .env
# 编辑 .env，把 EXPO_PUBLIC_API_URL 改成你自己的后端地址
```

注意事项：

- `EXPO_PUBLIC_API_URL` 会在**构建时被内联**进 APK，改完 `.env` 必须重新打包才生效。
- 建议使用 HTTPS（如 `https://api.example.com`），手机在任何网络下都能访问。
- ⚠️ 不要用 `http://` + 局域网 IP 打包正式 APK：**Android 9+ 默认禁止明文 HTTP**，release 包会连不上服务器。

### 3. 替换应用图标（可选）

只需准备**一张 1024×1024 的 PNG**，覆盖 `assets/images/icon.png` 即可：

- logo 放在**画面中央约 2/3 范围内**（系统自适应遮罩会裁掉边缘，贴边会被切掉）。
- 建议纯色/圆角背景、无透明边框，整体观感最佳。
- Expo 会自动基于 `icon.png` 生成 Android 自适应图标（前景 + 背景色 `#f5f7fa`）。
- 若想进一步做 Android 13+ 主题图标，可在 `app.json` 的 `android` 下补 `adaptiveIcon.monochromeImage`（非必须）。

## 三、打包 APK

### 1. 登录 Expo

```bash
npx eas-cli login
```

### 2. 初始化 EAS 项目（仅首次）

```bash
npx eas-cli init
```

（会把 `projectId` 写入 `app.json`；之后同一仓库再打包可跳过。）

### 3. 构建

```bash
npx eas-cli build -p android --profile preview
```

- `eas.json` 中 `preview` 配置为 `distribution: internal` + `buildType: apk`，产出**可直接安装的 APK**（而非上架 Google Play 用的 AAB）。
- 签名由 EAS 自动管理；同一项目后续更新会复用同一 keystore，可覆盖安装旧包。
- 构建在云端进行，免费档个人项目足够。

### 4. 安装

- 构建完成后终端会给出下载链接，手机浏览器打开下载安装即可。
- 或用数据线连接手机：`adb install app-release.apk`。

## 四、常见问题

| 现象 | 处理 |
|---|---|
| 构建提示 expo-notifications 需要 FCM / google-services.json | 本 App 只使用**本地通知**（前台轮询待审数），不依赖 FCM。检查 `app.json` 是否误加了 `googleServicesFile`，移除即可；仅做远程推送才需要 Firebase |
| APK 装到手机后连不上服务器 | 确认 `.env` 的地址在手机当前网络下可访问（局域网 IP 换网后会失效）；release 包默认拦截明文 `http://` |
| 改了 `app.json` / 图标 / `.env` 没生效 | EAS 每次构建都会重新生成原生工程，重新构建即可 |
| 新包无法覆盖安装旧包 | 递增 `app.json` 里 `expo.version`（或补 `android.versionCode`），保持每次发布版本号上升 |
