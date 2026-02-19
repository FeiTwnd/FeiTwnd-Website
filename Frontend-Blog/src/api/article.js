import request from '@/utils/request'

/** 获取已发布文章列表（分页） */
export const getArticlePage = (page = 1, pageSize = 10) =>
  request.get('/blog/article/page', { params: { page, pageSize } })

/** 根据 slug 获取文章详情 */
export const getArticleBySlug = (slug) =>
  request.get(`/blog/article/detail/${slug}`)

/** 根据分类ID获取文章列表（分页） */
export const getArticlesByCategory = (categoryId, page = 1, pageSize = 10) =>
  request.get(`/blog/article/category/${categoryId}`, {
    params: { page, pageSize }
  })

/** 获取文章归档（按年月分组） */
export const getArticleArchive = () => request.get('/blog/article/archive')

/** 文章搜索 */
export const searchArticles = (keyword, page = 1, pageSize = 10) =>
  request.get('/blog/article/search', { params: { keyword, page, pageSize } })
