import request from '@/utils/request'

// 获取个人信息
export const getPersonalInfoAPI = () => {
  return request.get('/home/personalInfo')
}
