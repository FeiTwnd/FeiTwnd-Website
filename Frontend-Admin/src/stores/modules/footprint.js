import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getFootprintList,
  createFootprint,
  updateFootprint,
  deleteFootprints,
  getCityImages,
  createCityImage,
  updateCityImage,
  deleteCityImage
} from '@/api/footprint'

export const useFootprintStore = defineStore('footprint', () => {
  const list = ref([])
  const total = ref(0)
  const loading = ref(false)

  /** 分页查询城市足迹 */
  const fetchList = async (params = {}) => {
    loading.value = true
    try {
      const res = await getFootprintList(params)
      if (res.data) {
        list.value = res.data.records ?? []
        total.value = res.data.total ?? 0
      }
    } finally {
      loading.value = false
    }
  }

  const saveFootprint = async (data) => {
    if (data.id) {
      await updateFootprint(data)
    } else {
      await createFootprint(data)
    }
  }

  const removeFootprints = async (ids) => {
    await deleteFootprints(ids)
  }

  /* ---- 城市图片 ---- */
  const images = ref([])
  const imagesLoading = ref(false)

  const fetchCityImages = async (cityId) => {
    imagesLoading.value = true
    try {
      const res = await getCityImages(cityId)
      images.value = res.data ?? []
    } finally {
      imagesLoading.value = false
    }
  }

  const saveCityImage = async (data) => {
    if (data.id) {
      await updateCityImage(data)
    } else {
      await createCityImage(data)
    }
    await fetchCityImages(data.cityId)
  }

  const removeCityImage = async (id, cityId) => {
    await deleteCityImage(id)
    await fetchCityImages(cityId)
  }

  return {
    list,
    total,
    loading,
    fetchList,
    saveFootprint,
    removeFootprints,
    images,
    imagesLoading,
    fetchCityImages,
    saveCityImage,
    removeCityImage
  }
})
