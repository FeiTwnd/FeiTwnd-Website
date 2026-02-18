import http from '@/utils/request'

/**
 * 分页条件查询留言
 * @param {{ page: number, pageSize: number, isApproved?: number }} params
 */
export const getMessagePage = (params) =>
  http.get('/admin/message/page', { params })

/**
 * 批量审核通过留言
 * @param {number[]} ids
 */
export const approveMessages = (ids) =>
  http.put('/admin/message/approve', null, { params: { ids: ids.join(',') } })

/**
 * 批量删除留言
 * @param {number[]} ids
 */
export const deleteMessages = (ids) =>
  http.delete('/admin/message', { params: { ids: ids.join(',') } })

/**
 * 管理员回复留言
 * @param {{ parentId: number, content: string }} data
 */
export const replyMessage = (data) => http.post('/admin/message/reply', data)
