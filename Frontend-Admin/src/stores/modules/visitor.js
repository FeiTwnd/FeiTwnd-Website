import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getVisitorPage, blockVisitors, unblockVisitors } from '@/api/visitor'

export const useVisitorStore = defineStore('visitor', () => {
  const list = ref([])
  const total = ref(0)
  const loading = ref(false)

  /**
   * 分页查询访客列表
   * @param {{ page: number, pageSize: number, ip?: string, isBlocked?: number }} query
   */
  const fetchList = async (query) => {
    loading.value = true
    try {
      const res = await getVisitorPage(query)
      list.value = res.data?.records ?? []
      total.value = res.data?.total ?? 0
    } finally {
      loading.value = false
    }
  }

  /** 批量封禁 @param {number[]} ids */
  const block = async (ids) => await blockVisitors(ids)

  /** 批量解封 @param {number[]} ids */
  const unblock = async (ids) => await unblockVisitors(ids)

  return { list, total, loading, fetchList, block, unblock }
})
