import http from '@/utils/request'

/**
 * 根据类型获取经历列表
 * @param {number|undefined} type  0-教育, 1-工作, undefined-全部
 */
export const getExperienceList = (type) =>
  http.get('/admin/experience', { params: type !== undefined ? { type } : {} })

/**
 * 新增经历
 * @param {Object} data
 */
export const createExperience = (data) => http.post('/admin/experience', data)

/**
 * 更新经历
 * @param {Object} data
 */
export const updateExperience = (data) => http.put('/admin/experience', data)

/**
 * 批量删除经历
 * @param {number[]} ids
 */
export const deleteExperiences = (ids) =>
  http.delete('/admin/experience', { params: { ids: ids.join(',') } })
