import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getFriendLinkList,
  createFriendLink,
  updateFriendLink,
  deleteFriendLinks
} from '@/api/friendLink'

export const useFriendLinkStore = defineStore('friendLink', () => {
  const list = ref([])
  const loading = ref(false)

  /** 拉取所有友情链接 */
  const fetchList = async () => {
    loading.value = true
    try {
      const res = await getFriendLinkList()
      list.value = res.data ?? []
    } finally {
      loading.value = false
    }
  }

  /**
   * 保存友情链接（有 id 则更新，无 id 则新建）
   * @param {Object} data
   */
  const saveFriendLink = async (data) => {
    if (data.id) {
      await updateFriendLink(data)
    } else {
      await createFriendLink(data)
    }
    await fetchList()
  }

  /**
   * 批量删除
   * @param {number[]} ids
   */
  const removeFriendLinks = async (ids) => {
    await deleteFriendLinks(ids)
    await fetchList()
  }

  return { list, loading, fetchList, saveFriendLink, removeFriendLinks }
})
