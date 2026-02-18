import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getProfile, logout } from '@/api/auth'

export const useUserStore = defineStore(
  'user',
  () => {
    const token = ref(localStorage.getItem('admin_token') || '')
    const userInfo = ref({})

    /** 持久化写入 Token */
    const setToken = (newToken) => {
      token.value = newToken
      localStorage.setItem('admin_token', newToken)
    }

    const setUserInfo = (info) => (userInfo.value = info)

    /** 清除登录状态 */
    const clearUserInfo = () => {
      token.value = ''
      userInfo.value = {}
      localStorage.removeItem('admin_token')
    }

    /**
     * 登录动作：调用 API → 存 Token → 拉取用户信息 → 跳转
     * @param {{ username: string, password: string, code: string }} payload
     */
    const loginAction = async (payload) => {
      const res = await login(payload)
      setToken(res.data.token)
      setUserInfo({ id: res.data.id })
      try {
        await fetchUserInfo()
      } catch {
        // 个人信息获取失败不影响登录跳转
      }
      const r = await import('@/router')
      r.default.push('/dashboard')
    }

    /** 拉取当前管理员信息 */
    const fetchUserInfo = async () => {
      if (!token.value) return
      const res = await getProfile()
      userInfo.value = res.data || {}
    }

    /** 退出登录 */
    const logoutAction = async () => {
      try {
        if (userInfo.value?.id) {
          await logout({ id: userInfo.value.id, token: token.value })
        }
      } finally {
        clearUserInfo()
        ElMessage.success('已退出登录')
        const r = await import('@/router')
        r.default.push('/login')
      }
    }

    /** 是否已登录（供路由守卫调用） */
    const isLoggedIn = () => !!token.value

    return {
      token,
      userInfo,
      isLoggedIn,
      setToken,
      setUserInfo,
      clearUserInfo,
      loginAction,
      fetchUserInfo,
      logoutAction
    }
  },
  { persist: true }
)
