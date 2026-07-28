import axios from 'axios'
import router from '@/router'

const baseURL = '/api'

/**
 * Axios 实例
 */
const http = axios.create({
  baseURL,
  timeout: 300000
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
    // 业务错误（HTTP 200 + code≠1）：统一展示后端返回的错误信息
    ElMessage.error(data?.msg || '请求失败')
    return Promise.reject(data)
  },
  (error) => {
    const status = error?.response?.status
    // 后端约束/校验异常以非 2xx 状态码返回，错误信息在 response.data.msg
    const backendMsg = error?.response?.data?.msg
    if (status === 401) {
      // 防止多个并发请求同时 401 弹出多个提示
      if (!http._isRedirecting401) {
        http._isRedirecting401 = true
        ElMessage.warning('登录状态失效，请重新登录')
        localStorage.removeItem('admin_token')
        const currentPath = router.currentRoute.value?.fullPath || '/dashboard'
        const redirect = currentPath === '/login' ? '/dashboard' : currentPath
        router.push({ path: '/login', query: { redirect } })
        setTimeout(() => {
          http._isRedirecting401 = false
        }, 2000)
      }
    } else if (status === 403) {
      ElMessage.error(backendMsg || '权限不足，无法执行该操作')
    } else {
      // 优先展示后端错误信息（如"Slug 已存在"），无则按场景兜底
      ElMessage.error(backendMsg || '网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default http
export { baseURL }
