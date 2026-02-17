import request from '@/utils/request'

//登录接口
export const loginAPI = (data) => {
  return request.post('/login', data)
}
