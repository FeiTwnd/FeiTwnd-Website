import axios from 'axios'
import router from '@/router'

const baseURL = '/api'

const instance = axios.create({
  baseURL,
  timeout: 10000
})

instance.interceptors.request.use(
  (config) => config,
  (err) => Promise.reject(err)
)

instance.interceptors.response.use(
  (res) => {
    if (res.data.code === 1) {
      return res
    }
    // 业务错误（HTTP 200 + code≠1）：统一展示后端返回的错误信息
    ElMessage.error(res.data?.msg || '请求失败')
    return Promise.reject(res.data)
  },
  (err) => {
    if (err?.response?.status === 403) {
      router.replace('/403')
      return Promise.reject(err)
    }
    // 后端约束/校验异常以非 2xx 状态码返回，错误信息在 response.data.msg
    ElMessage.error(err?.response?.data?.msg || '网络错误，请稍后重试')
    return Promise.reject(err)
  }
)

export default instance
export { baseURL }
