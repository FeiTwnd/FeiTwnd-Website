import http from '@/utils/request'

/**
 * 查询 AI 模块状态
 * 后端未打包 AI 模块时接口 404，返回 null，调用方应隐藏 AI 相关入口
 * @returns {Promise<{enabled: boolean, modelName: string} | null>}
 */
export const getAiStatus = async () => {
  try {
    const res = await http.get('/admin/ai/status')
    return res?.data ?? null
  } catch {
    return null
  }
}
