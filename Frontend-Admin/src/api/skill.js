import http from '@/utils/request'

/** 获取所有技能 */
export const getSkillList = () => http.get('/admin/skill')

/**
 * 新增技能
 * @param {Object} data
 */
export const createSkill = (data) => http.post('/admin/skill', data)

/**
 * 更新技能
 * @param {Object} data
 */
export const updateSkill = (data) => http.put('/admin/skill', data)

/**
 * 批量删除技能
 * @param {number[]} ids
 */
export const deleteSkills = (ids) =>
  http.delete('/admin/skill', { params: { ids: ids.join(',') } })
