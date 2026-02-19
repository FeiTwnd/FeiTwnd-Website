import request from '@/utils/request'

/** 获取有已发布文章的标签列表 */
export const getTags = () => request.get('/blog/article/tag')

/** 根据标签ID获取已发布文章列表（分页） */
export const getArticlesByTag = (tagId, page = 1, pageSize = 10) =>
  request.get(`/blog/article/tag/${tagId}`, { params: { page, pageSize } })
