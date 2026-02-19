import request from '@/utils/request'

/** 根据配置键获取系统配置 */
export const getConfigByKey = (key) =>
  request.get(`/blog/systemConfig/key/${key}`)
