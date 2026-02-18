import axios from 'axios'

const baseURL = '/api'

/**
 * Axios 实例
 */
const http = axios.create({
  baseURL,
  timeout: 15000
})

/**
 * 读取本地 Token
 * @returns {string}
 */
const getToken = () => {
  return localStorage.getItem('admin_token') || ''
}

http.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers = config.headers || {}
      config.headers['Authorization'] = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

http.interceptors.response.use(
  (response) => {
    const { data } = response
    if (data?.code === 1) {
      return data
    }
    ElMessage.error(data?.msg || '请求失败')
    return Promise.reject(data)
  },
  (error) => {
    const status = error?.response?.status
    if (status === 401) {
      ElMessage.warning('登录状态失效，请重新登录')
      localStorage.removeItem('admin_token')
      import('@/router').then((m) => m.default.push('/login'))
    } else if (status === 403) {
      ElMessage.error('权限不足，无法执行该操作')
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default http
export { baseURL }
