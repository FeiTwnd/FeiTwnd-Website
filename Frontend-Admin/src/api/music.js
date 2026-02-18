import http from '@/utils/request'

/**
 * 分页查询音乐列表
 * @param {{ page: number, pageSize: number, title?: string }} params
 */
export const getMusicPage = (params) =>
  http.get('/admin/music/page', { params })

/**
 * 根据 ID 获取音乐详情
 * @param {number} id
 */
export const getMusicById = (id) => http.get(`/admin/music/${id}`)

/**
 * 新增音乐
 * @param {Object} data
 */
export const createMusic = (data) => http.post('/admin/music', data)

/**
 * 更新音乐
 * @param {Object} data
 */
export const updateMusic = (data) => http.put('/admin/music', data)

/**
 * 批量删除音乐
 * @param {number[]} ids
 */
export const deleteMusics = (ids) =>
  http.delete('/admin/music', { params: { ids: ids.join(',') } })
