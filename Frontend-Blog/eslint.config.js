import { defineConfig } from 'eslint/config'
import globals from 'globals'
import js from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'
import prettierPlugin from 'eslint-plugin-prettier'

export default defineConfig([
  {
    files: ['**/*.{js,vue}'],
    ignores: ['**/dist/**', '**/coverage/**'],
    languageOptions: {
      globals: {
        ...globals.browser,
        ...globals.node, // 如果需要 Node.js 全局变量
        ElMessage: 'readonly', // Element Plus 消息提示
        ElMessageBox: 'readonly', // Element Plus 消息弹窗
        ElLoading: 'readonly', // Element Plus 加载动画
        ElNotification: 'readonly', // Element Plus 通知
        ElDialog: 'readonly', // Element Plus 对话框
        ElTable: 'readonly' // Element Plus 表格
      }
    },
    plugins: {
      vue: pluginVue,
      prettier: prettierPlugin
    },
    rules: {
      // Prettier 规则
      'prettier/prettier': [
        'warn',
        {
          singleQuote: true,
          semi: false,
          printWidth: 80,
          trailingComma: 'none',
          endOfLine: 'auto'
        }
      ],
      // Vue 规则
      'vue/multi-word-component-names': [
        'warn',
        {
          ignores: ['index']
        }
      ],
      'vue/no-setup-props-destructure': ['off'],
      // 其他规则
      'no-undef': 'error'
    }
  },
  js.configs.recommended, // ESLint 推荐规则
  ...pluginVue.configs['flat/essential'] // Vue 必要规则
])
