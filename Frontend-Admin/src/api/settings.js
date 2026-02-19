import http from '@/utils/request'

/** 根据配置键获取配置 */
export const getConfigByKey = (configKey) =>
  http.get(`/admin/systemConfig/key/${configKey}`)

/** 获取所有系统配置 */
export const getConfigList = () => http.get('/admin/systemConfig')

/**
 * 新增配置
 * @param {{ configKey: string, configValue?: string, configType?: string, description?: string }} data
 */
export const createConfig = (data) => http.post('/admin/systemConfig', data)

/**
 * 更新配置
 * @param {{ id: number, configKey: string, configValue?: string }} data
 */
export const updateConfig = (data) => http.put('/admin/systemConfig', data)

/**
 * 批量删除配置
 * @param {number[]} ids
 */
export const deleteConfigs = (ids) =>
  http.delete('/admin/systemConfig', { params: { ids: ids.join(',') } })

/** 获取个人信息 */
export const getPersonalInfo = () => http.get('/admin/personalInfo')

/**
 * 更新个人信息
 * @param {PersonalInfoDTO} data
 */
export const updatePersonalInfo = (data) =>
  http.put('/admin/personalInfo', data)

/**
 * 上传文件（图片等）
 * @param {FormData} formData
 */
export const uploadFile = (formData) =>
  http.post('/admin/common/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
