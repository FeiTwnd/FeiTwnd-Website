import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getMusicPage, createMusic, updateMusic, deleteMusics } from '@/api/music'

export const useMusicStore = defineStore('music', () => {
  const list = ref([])
  const total = ref(0)
  const loading = ref(false)

  /**
   * @param {{ page: number, pageSize: number, title?: string }} query
   */
  const fetchList = async (query) => {
    loading.value = true
    try {
      const res = await getMusicPage(query)
      list.value = res.data?.records ?? []
      total.value = res.data?.total ?? 0
    } finally {
      loading.value = false
    }
  }

  const saveMusic = async (data) => {
    if (data.id) {
      await updateMusic(data)
    } else {
      await createMusic(data)
    }
  }

  /** @param {number[]} ids */
  const remove = async (ids) => await deleteMusics(ids)

  return { list, total, loading, fetchList, saveMusic, remove }
})
