import http from '@/utils/request'

/**
 * 分页条件查询评论
 * @param {{ page: number, pageSize: number, articleId?: number, isApproved?: number, startTime?: string, endTime?: string }} params
 */
export const getCommentPage = (params) =>
  http.get('/admin/article/comment/page', { params })

/**
 * 批量审核通过评论
 * @param {number[]} ids
 */
export const approveComments = (ids) =>
  http.put('/admin/article/comment/approve', null, {
    params: { ids: ids.join(',') }
  })

/**
 * 批量删除评论
 * @param {number[]} ids
 */
export const deleteComments = (ids) =>
  http.delete('/admin/article/comment', { params: { ids: ids.join(',') } })

/**
 * 管理员回复评论
 * @param {{ articleId: number, parentId: number, content: string, parentNickname?: string }} data
 */
export const replyComment = (data) =>
  http.post('/admin/article/comment/reply', data)
