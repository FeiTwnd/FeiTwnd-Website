import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore(
  'theme',
  () => {
    // 'light' | 'dark' | 'auto'
    const mode = ref('auto')

    const applyTheme = () => {
      const prefersDark = window.matchMedia(
        '(prefers-color-scheme: dark)'
      ).matches
      const isDark =
        mode.value === 'dark' || (mode.value === 'auto' && prefersDark)
      document.documentElement.classList.toggle('dark', isDark)
    }

    // 响应系统主题变化
    const mql = window.matchMedia('(prefers-color-scheme: dark)')
    mql.addEventListener('change', () => {
      if (mode.value === 'auto') applyTheme()
    })

    const toggle = () => {
      const prefersDark = mql.matches
      const currentIsDark =
        mode.value === 'dark' || (mode.value === 'auto' && prefersDark)
      mode.value = currentIsDark ? 'light' : 'dark'
    }

    watch(mode, applyTheme, { immediate: true })

    return { mode, toggle, applyTheme }
  },
  {
    persist: {
      pick: ['mode']
    }
  }
)
