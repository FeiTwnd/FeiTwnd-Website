import http from '@/utils/request'

/**
 * 分页查询浏览记录
 * @param {{ page: number, pageSize: number, visitorId?: number, articleId?: number }} params
 */
export const getViewRecordPage = (params) =>
  http.get('/admin/view/page', { params })

/**
 * 批量删除浏览记录
 * @param {number[]} ids
 */
export const deleteViewRecords = (ids) =>
  http.delete('/admin/view', { params: { ids: ids.join(',') } })
