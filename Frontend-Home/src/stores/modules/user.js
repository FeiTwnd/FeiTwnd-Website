import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore(
  'user',
  () => {
    const userInfo = ref({}) // 定义用户信息

    return {
      userInfo
    }
  },
  {
    persist: false
  }
)
