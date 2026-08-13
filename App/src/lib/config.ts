/**
 * 全局配置：所有环境相关的值统一从这里读取。
 *
 * EXPO_PUBLIC_* 变量由 Expo CLI 在构建时从项目根目录的 .env 文件注入，
 * 并静态内联进客户端 bundle（必须用点号引用 process.env.EXPO_PUBLIC_*）。
 * 参考 https://docs.expo.dev/guides/environment-variables/
 */
export const API_BASE_URL = process.env.EXPO_PUBLIC_API_URL?.trim().replace(/\/+$/, '');

if (!API_BASE_URL) {
  throw new Error('缺少 EXPO_PUBLIC_API_URL：请在项目根目录 .env 中配置服务器地址（参考 .env.example）');
}
