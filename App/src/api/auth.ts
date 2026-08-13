import { api } from '@/lib/api-client';
import type { AdminVO } from '@/lib/types';

export function sendCode(username: string) {
  return api('/admin/admin/sendCode', { method: 'POST', body: { username } });
}

export function login(username: string, password: string, code: string) {
  return api<{ id: number; token: string }>('/admin/admin/login', {
    method: 'POST',
    body: { username, password, code },
  });
}

export function getProfile() {
  return api<AdminVO>('/admin/admin');
}

export function logout(id: number, token: string) {
  return api('/admin/admin/logout', { method: 'POST', body: { id, token } });
}

export function changePassword(oldPassword: string, newPassword: string, confirmNewPassword: string) {
  return api('/admin/admin/changePassword', {
    method: 'PUT',
    body: { oldPassword, newPassword, confirmNewPassword },
  });
}

export function changeNickname(nickname: string) {
  return api('/admin/admin/changeNickname', {
    method: 'PUT',
    body: { nickname },
  });
}