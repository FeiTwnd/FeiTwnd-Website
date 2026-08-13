import { api } from '@/lib/api-client';
import type { ArticleCategory, ArticleDetail, ArticleItem, ArticleTag, PageResult } from '@/lib/types';

type ArticlePageQuery = {
  page?: number;
  pageSize?: number;
  title?: string;
  isPublished?: number;
};

export function getArticles(query: ArticlePageQuery) {
  return api<PageResult<ArticleItem>>('/admin/article/page', { query });
}

export function getArticle(id: number) {
  return api<ArticleDetail>(`/admin/article/${id}`);
}

export function setPublished(id: number, isPublished: number) {
  return api(`/admin/article/publish/${id}`, {
    method: 'PUT',
    query: { isPublished },
  });
}

export function setTop(id: number, isTop: number) {
  return api(`/admin/article/top/${id}`, {
    method: 'PUT',
    query: { isTop },
  });
}

export function deleteArticles(ids: number[]) {
  return api('/admin/article', { method: 'DELETE', query: { ids: ids.join(',') } });
}

// —— 文章分类 ——
export function getCategories() {
  return api<ArticleCategory[]>('/admin/articleCategory');
}

export function createCategory(body: unknown) {
  return api('/admin/articleCategory', { method: 'POST', body });
}

export function updateCategory(body: unknown) {
  return api('/admin/articleCategory', { method: 'PUT', body });
}

export function deleteCategory(ids: number[]) {
  return api('/admin/articleCategory', { method: 'DELETE', query: { ids: ids.join(',') } });
}

// —— 文章标签 ——
export function getTags() {
  return api<ArticleTag[]>('/admin/article/tag');
}

export function createTag(body: unknown) {
  return api('/admin/article/tag', { method: 'POST', body });
}

export function updateTag(body: unknown) {
  return api('/admin/article/tag', { method: 'PUT', body });
}

export function deleteTag(ids: number[]) {
  return api('/admin/article/tag', { method: 'DELETE', query: { ids: ids.join(',') } });
}