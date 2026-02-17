import { useUserStore } from '@/stores'
import axios from 'axios'
import router from '@/router'

const baseURL = '/api'

const instance = axios.create({
  baseURL,
  timeout: 100000
})

instance.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.token = userStore.token
    }
    return config
  },
  (err) => Promise.reject(err)
)

instance.interceptors.response.use(
  (res) => {
    if (res.data.code === 1) {
      return res
    }
    return Promise.reject(res.data)
  },
  (err) => {
    console.log(err)
    if (err.response?.status === 401) {
      const userStore = useUserStore()
      userStore.clearToken()
      router.push('/login')
    }
    return Promise.reject(err)
  }
)

export default instance
export { baseURL }
