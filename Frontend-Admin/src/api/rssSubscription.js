import http from '@/utils/request'

/**
 * 分页查询 RSS 订阅
 * @param {{ page: number, pageSize: number, email?: string }} params
 */
export const getRssPage = (params) =>
  http.get('/admin/rssSubscription/page', { params })

/** 获取所有激活订阅 */
export const getAllActiveRss = () => http.get('/admin/rssSubscription')

/**
 * 根据 ID 查询
 * @param {number} id
 */
export const getRssById = (id) => http.get(`/admin/rssSubscription/${id}`)

/**
 * 更新订阅状态
 * @param {Object} data
 */
export const updateRss = (data) => http.put('/admin/rssSubscription', data)

/**
 * 批量删除订阅
 * @param {number[]} ids
 */
export const deleteRssList = (ids) =>
  http.delete('/admin/rssSubscription', { params: { ids: ids.join(',') } })
