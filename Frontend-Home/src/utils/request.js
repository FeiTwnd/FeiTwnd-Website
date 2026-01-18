import axios from 'axios'

const baseURL = '/api'

const instance = axios.create({
  baseURL,
  timeout: 10000
})

instance.interceptors.request.use(
  (config) => {
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
    return Promise.reject(err)
  }
)

export default instance
export { baseURL }
