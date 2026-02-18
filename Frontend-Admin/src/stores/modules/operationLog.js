import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getOperationLogPage, deleteOperationLogs } from '@/api/operationLog'

export const useOperationLogStore = defineStore('operationLog', () => {
  const list = ref([])
  const total = ref(0)
  const loading = ref(false)

  /**
   * 分页查询操作日志
   * @param {{ page: number, pageSize: number, target?: string, operationType?: string }} query
   */
  const fetchList = async (query) => {
    loading.value = true
    try {
      const res = await getOperationLogPage(query)
      list.value = res.data?.records ?? []
      total.value = res.data?.total ?? 0
    } finally {
      loading.value = false
    }
  }

  /** @param {number[]} ids */
  const remove = async (ids) => await deleteOperationLogs(ids)

  return { list, total, loading, fetchList, remove }
})
