/**
 * 全局配置：所有环境相关的值统一从这里读取。
 *
 * EXPO_PUBLIC_* 变量由 Expo CLI 在构建时注入，并静态内联进客户端 bundle
 * （必须用点号引用 process.env.EXPO_PUBLIC_API_URL 这种形式才会被内联）。
 *
 * EXPO_PUBLIC_API_URL 的配置来源：
 * - EAS 云端构建：`npx eas-cli env:set EXPO_PUBLIC_API_URL <地址>`（存在 EAS 服务端，不入 git）
 * - 本地开发（expo start / Expo Go / expo export）：项目根目录 `.env`
 *
 * ⚠️ 此处不能 throw：bundle 求值期的异常会导致 App 启动即闪退（EAS 云端构建不读取本地 .env，
 * env 未注入时这里抛错 App 直接崩）。因此缺失时 API_BASE_URL 为空字符串，由登录页给出提示。
 */
export const API_BASE_URL = (process.env.EXPO_PUBLIC_API_URL ?? '').trim().replace(/\/+$/, '');
