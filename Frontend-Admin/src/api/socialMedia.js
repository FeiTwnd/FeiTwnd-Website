import http from '@/utils/request'

/** 获取所有社交媒体 */
export const getSocialMediaList = () => http.get('/admin/socialMedia')

/**
 * 新增社交媒体
 * @param {Object} data
 */
export const createSocialMedia = (data) => http.post('/admin/socialMedia', data)

/**
 * 更新社交媒体
 * @param {Object} data
 */
export const updateSocialMedia = (data) => http.put('/admin/socialMedia', data)

/**
 * 批量删除社交媒体
 * @param {number[]} ids
 */
export const deleteSocialMedias = (ids) =>
  http.delete('/admin/socialMedia', { params: { ids: ids.join(',') } })
