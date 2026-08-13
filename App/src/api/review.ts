import { api } from '@/lib/api-client';
import type { CommentItem, MessageItem, PageResult } from '@/lib/types';

type PageQuery = {
  page?: number;
  pageSize?: number;
  isApproved?: number;
};

export function getComments(query: PageQuery) {
  return api<PageResult<CommentItem>>('/admin/article/comment/page', { query });
}

export function approveComments(ids: number[]) {
  return api('/admin/article/comment/approve', {
    method: 'PUT',
    query: { ids: ids.join(',') },
  });
}

export function deleteComments(ids: number[]) {
  return api('/admin/article/comment', { method: 'DELETE', query: { ids: ids.join(',') } });
}

export function replyComment(payload: {
  articleId: number;
  parentId: number;
  rootId?: number | null;
  parentNickname?: string | null;
  content: string;
  isMarkdown: number;
}) {
  return api('/admin/article/comment/reply', { method: 'POST', body: payload });
}

export function getMessages(query: PageQuery) {
  return api<PageResult<MessageItem>>('/admin/message/page', { query });
}

export function approveMessages(ids: number[]) {
  return api('/admin/message/approve', {
    method: 'PUT',
    query: { ids: ids.join(',') },
  });
}

export function deleteMessages(ids: number[]) {
  return api('/admin/message', { method: 'DELETE', query: { ids: ids.join(',') } });
}

export function replyMessage(payload: {
  parentId: number;
  rootId?: number | null;
  parentNickname?: string | null;
  content: string;
  isMarkdown: number;
}) {
  return api('/admin/message/reply', { method: 'POST', body: payload });
}