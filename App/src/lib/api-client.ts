import { API_BASE_URL } from './config';
import { getToken } from './storage';
import { Platform } from 'react-native';

/**
 * 客户端标识头：后端据此区分请求来自浏览器管理端还是移动端 App。
 * App 环境没有浏览器 User-Agent，后端可读取这些头来记录设备信息（如站长回复的来源）。
 */
const CLIENT_TYPE = 'feitwnd-app';
const CLIENT_OS = Platform.OS === 'ios' ? 'iOS' : 'Android';

const REQUEST_TIMEOUT_MS = 15000;

export class ApiError extends Error {
  status?: number;

  constructor(message: string, status?: number) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

let onUnauthorized: (() => void) | null = null;

export function setUnauthorizedHandler(handler: () => void) {
  onUnauthorized = handler;
}

interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
  query?: Record<string, unknown>;
  body?: unknown;
}

/** fetch 封装：统一超时。超时按 ApiError 抛出，避免弱网下请求永久挂起 */
async function fetchWithTimeout(input: string, init: RequestInit): Promise<Response> {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);
  try {
    return await fetch(input, { ...init, signal: controller.signal });
  } catch (e) {
    if (controller.signal.aborted) throw new ApiError('请求超时，请检查网络后重试');
    throw e;
  } finally {
    clearTimeout(timer);
  }
}

export async function api<T = unknown>(path: string, options: RequestOptions = {}): Promise<T> {
  const token = await getToken();

  // API_BASE_URL 可能带路径前缀（如 /wo/chovy，由 nginx 剥离后转发）。
  // 用字符串拼接保留前缀；不能用 new URL(path, base)——绝对路径会替换掉 base 的路径段。
  let url = API_BASE_URL + path;
  if (options.query) {
    const pairs = Object.entries(options.query)
      .filter(([, v]) => v !== undefined)
      .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`);
    if (pairs.length > 0) url += '?' + pairs.join('&');
  }

  const headers: Record<string, string> = { Accept: 'application/json' };
  if (token) headers.Authorization = token;
  if (options.body !== undefined) headers['Content-Type'] = 'application/json';
  // 标记客户端来源与设备系统，供后端区分移动端 App（无浏览器 UA）场景
  headers['X-Client-Type'] = CLIENT_TYPE;
  headers['X-Client-OS'] = CLIENT_OS;

  const response = await fetchWithTimeout(url, {
    method: options.method ?? 'GET',
    headers,
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
  });

  if (response.status === 401) {
    onUnauthorized?.();
    throw new ApiError('登录状态失效，请重新登录', 401);
  }

  const text = await response.text();
  // 后端可能返回非 JSON（网关错误页等），统一按 ApiError 处理，避免抛 SyntaxError 破坏 instanceof 判断
  let data: { code?: number; msg?: string; data?: T } = {};
  try {
    data = text ? JSON.parse(text) : {};
  } catch {
    throw new ApiError(`服务器响应异常（${response.status}）`, response.status);
  }

  if (response.status !== 200) {
    throw new ApiError(data?.msg || `请求失败（${response.status}）`, response.status);
  }
  if (data?.code !== 1) {
    throw new ApiError(data?.msg || '请求失败');
  }
  return data.data as T;
}
