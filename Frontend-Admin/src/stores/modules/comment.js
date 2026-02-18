import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getCommentPage,
  approveComments,
  deleteComments,
  replyComment
} from '@/api/comment'

export const useCommentStore = defineStore('comment', () => {
  const list = ref([])
  const total = ref(0)
  const loading = ref(false)

  /**
   * @param {{ page: number, pageSize: number, articleId?: number, isApproved?: number }} query
   */
  const fetchList = async (query) => {
    loading.value = true
    try {
      const res = await getCommentPage(query)
      list.value = res.data?.records || []
      total.value = res.data?.total || 0
    } finally {
      loading.value = false
    }
  }

  const approve = async (ids) => await approveComments(ids)
  const remove = async (ids) => await deleteComments(ids)
  const reply = async (data) => await replyComment(data)

  return { list, total, loading, fetchList, approve, remove, reply }
})
