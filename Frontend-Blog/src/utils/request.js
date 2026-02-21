import axios from 'axios'
import router from '@/router'

const baseURL = '/api'

const instance = axios.create({
  baseURL,
  timeout: 15000
})

instance.interceptors.response.use(
  (res) => {
    if (res.data.code === 1) {
      return res
    }
    return Promise.reject(res.data)
  },
  (err) => {
    if (err?.response?.status === 403) {
      router.replace('/403')
    }
    return Promise.reject(err)
  }
)

export default instance
export { baseURL }
