import request from '@/utils/request'

// 获取社交媒体信息
export const getSocialMediaAPI = () => {
  return request.get('/home/socialMedia')
}
