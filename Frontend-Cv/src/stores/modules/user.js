import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore(
  'user',
  () => {
    const token = ref('') // 定义 token
    const userInfo = ref({}) // 定义用户信息

    const setToken = (newToken) => (token.value = newToken) // 设置 token
    const setUserInfo = (newUserInfo) => (userInfo.value = newUserInfo) // 设置用户信息

    // 清除用户信息
    const clearUserInfo = () => {
      token.value = ''
      userInfo.value = ''
    }

    //判断用户是否登录
    const isLoggedIn = () => !!token.value

    return {
      token,
      userInfo,
      setToken,
      setUserInfo,
      clearUserInfo,
      isLoggedIn
    }
  },
  {
    persist: true
  }
)
