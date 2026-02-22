import http from '@/utils/request'

/**
 * 分页条件查询文章列表
 * @param {{ page: number, pageSize: number, title?: string, categoryId?: number, isPublished?: number }} params
 */
export const getArticlePage = (params) =>
  http.get('/admin/article/page', { params })

/**
 * 根据 ID 获取文章详情
 * @param {number} id
 */
export const getArticleById = (id) => http.get(`/admin/article/${id}`)

/**
 * 创建文章
 * @param {ArticleDTO} data
 */
export const createArticle = (data) => http.post('/admin/article', data)

/**
 * 更新文章
 * @param {ArticleDTO} data
 */
export const updateArticle = (data) => http.put('/admin/article', data)

/**
 * 批量删除文章
 * @param {number[]} ids
 */
export const deleteArticles = (ids) =>
  http.delete('/admin/article', { params: { ids: ids.join(',') } })

/**
 * 发布 / 取消发布
 * @param {number} id
 * @param {0|1} isPublished
 */
export const togglePublish = (id, isPublished) =>
  http.put(`/admin/article/publish/${id}`, null, { params: { isPublished } })

/**
 * 置顶 / 取消置顶
 * @param {number} id
 * @param {0|1} isTop
 */
export const toggleTop = (id, isTop) =>
  http.put(`/admin/article/top/${id}`, null, { params: { isTop } })

/**
 * 文章搜索
 * @param {{ keyword: string, page?: number, pageSize?: number }} params
 */
export const searchArticles = (params) =>
  http.get('/admin/article/search', { params })
