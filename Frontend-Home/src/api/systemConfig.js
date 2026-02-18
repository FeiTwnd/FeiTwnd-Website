import request from '@/utils/request'

// 获取系统配置信息
export const getSystemConfigAPI = (configKey) => {
  return request.get(`/home/systemConfig/key/${configKey}`)
}
