import { ApiError } from '@/lib/api-client';
import { API_BASE_URL } from '@/lib/config';
import { getToken } from '@/lib/storage';

/**
 * 上传图片到 OSS（multipart/form-data），返回图片 URL。
 * 复用统一响应格式 { code, msg, data }，但与 JSON 客户端分开实现，
 * 因为 fetch 的 FormData 需要由运行时生成 multipart 边界，不能手动设 Content-Type。
 */
export async function uploadImage(
  uri: string,
  name = 'upload.jpg',
  mime = 'image/jpeg'
): Promise<string> {
  const token = await getToken();

  const form = new FormData();
  // React Native 的 FormData 用 { uri, name, type } 描述本地文件
  form.append('file', { uri, name, type: mime } as unknown as Blob);

  const response = await fetch(`${API_BASE_URL}/admin/common/upload`, {
    method: 'POST',
    headers: token ? { Authorization: token } : undefined,
    body: form,
  });

  const data = await response.json().catch(() => ({}));
  if (response.status !== 200 || data?.code !== 1) {
    throw new ApiError(data?.msg || `上传失败（${response.status}）`, response.status);
  }
  return data.data as string;
}
