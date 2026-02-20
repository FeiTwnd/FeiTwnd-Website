import axios from 'axios'

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
    return Promise.reject(err)
  }
)

export default instance
export { baseURL }
