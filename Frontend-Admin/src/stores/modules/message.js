import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getMessagePage,
  approveMessages,
  deleteMessages,
  replyMessage
} from '@/api/message'

export const useMessageStore = defineStore('message', () => {
  const list = ref([])
  const total = ref(0)
  const loading = ref(false)

  /**
   * 分页拉取留言列表
   * @param {{ page: number, pageSize: number, isApproved?: number }} query
   */
  const fetchList = async (query) => {
    loading.value = true
    try {
      const res = await getMessagePage(query)
      list.value = res.data?.records ?? []
      total.value = res.data?.total ?? 0
    } finally {
      loading.value = false
    }
  }

  /** @param {number[]} ids */
  const approve = async (ids) => await approveMessages(ids)

  /** @param {number[]} ids */
  const remove = async (ids) => await deleteMessages(ids)

  /** @param {{ parentId: number, content: string }} data */
  const reply = async (data) => await replyMessage(data)

  return { list, total, loading, fetchList, approve, remove, reply }
})
