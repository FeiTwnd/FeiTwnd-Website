import http from '@/utils/request'

/**
 * 分页查询访客列表
 * @param {{ page: number, pageSize: number, country?: string, province?: string, city?: string, status?: number }} params
 */
export const getVisitorPage = (params) =>
  http.get('/admin/visitor/page', { params })

/**
 * 批量封禁访客
 * @param {number[]} ids
 */
export const blockVisitors = (ids) =>
  http.put('/admin/visitor/block', null, { params: { ids: ids.join(',') } })

/**
 * 批量解封访客
 * @param {number[]} ids
 */
export const unblockVisitors = (ids) =>
  http.put('/admin/visitor/unblock', null, { params: { ids: ids.join(',') } })
