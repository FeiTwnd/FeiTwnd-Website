import { API_BASE_URL } from './config';
import { getToken } from './storage';

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

export async function api<T = unknown>(path: string, options: RequestOptions = {}): Promise<T> {
  const token = await getToken();

  const url = new URL(path, API_BASE_URL);
  if (options.query) {
    for (const [key, value] of Object.entries(options.query)) {
      if (value !== undefined) url.searchParams.set(key, String(value));
    }
  }

  const headers: Record<string, string> = { Accept: 'application/json' };
  if (token) headers.Authorization = token;
  if (options.body !== undefined) headers['Content-Type'] = 'application/json';

  const response = await fetch(url, {
    method: options.method ?? 'GET',
    headers,
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
  });

  if (response.status === 401) {
    onUnauthorized?.();
    throw new ApiError('登录状态失效，请重新登录', 401);
  }

  const text = await response.text();
  const data = text ? JSON.parse(text) : {};

  if (response.status !== 200) {
    throw new ApiError(data?.msg || `请求失败（${response.status}）`, response.status);
  }
  if (data?.code !== 1) {
    throw new ApiError(data?.msg || '请求失败');
  }
  return data.data as T;
}