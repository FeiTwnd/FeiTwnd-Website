import { api } from '@/lib/api-client';
import type { PageResult, VisitorItem } from '@/lib/types';

export function getVisitors(query: { page?: number; pageSize?: number; isBlocked?: number }) {
  return api<PageResult<VisitorItem>>('/admin/visitor/page', { query });
}

export function blockVisitors(ids: number[]) {
  return api('/admin/visitor/block', {
    method: 'PUT',
    query: { ids: ids.join(',') },
  });
}

export function unblockVisitors(ids: number[]) {
  return api('/admin/visitor/unblock', {
    method: 'PUT',
    query: { ids: ids.join(',') },
  });
}