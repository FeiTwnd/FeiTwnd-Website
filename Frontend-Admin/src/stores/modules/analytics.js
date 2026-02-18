import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getRssPage, updateRss, deleteRssList } from '@/api/rssSubscription'
import { getViewRecordPage, deleteViewRecords } from '@/api/viewRecord'

export const useAnalyticsStore = defineStore('analytics', () => {
  /* ---- RSS Subscription ---- */
  const rssList = ref([])
  const rssTotal = ref(0)
  const rssLoading = ref(false)

  /** @param {{ page: number, pageSize: number }} query */
  const fetchRssList = async (query) => {
    rssLoading.value = true
    try {
      const res = await getRssPage(query)
      rssList.value = res.data?.records ?? []
      rssTotal.value = res.data?.total ?? 0
    } finally {
      rssLoading.value = false
    }
  }

  const saveRss = async (data) => await updateRss(data)

  /** @param {number[]} ids */
  const removeRss = async (ids) => await deleteRssList(ids)

  /* ---- View Records ---- */
  const viewList = ref([])
  const viewTotal = ref(0)
  const viewLoading = ref(false)

  /** @param {{ page: number, pageSize: number }} query */
  const fetchViewList = async (query) => {
    viewLoading.value = true
    try {
      const res = await getViewRecordPage(query)
      viewList.value = res.data?.records ?? []
      viewTotal.value = res.data?.total ?? 0
    } finally {
      viewLoading.value = false
    }
  }

  /** @param {number[]} ids */
  const removeViewRecords = async (ids) => await deleteViewRecords(ids)

  return {
    rssList,
    rssTotal,
    rssLoading,
    fetchRssList,
    saveRss,
    removeRss,
    viewList,
    viewTotal,
    viewLoading,
    fetchViewList,
    removeViewRecords
  }
})
