import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getCategoryList,
  createCategory,
  updateCategory,
  deleteCategories,
  getTagList,
  createTag,
  updateTag,
  deleteTags
} from '@/api/category'

export const useCategoryStore = defineStore('category', () => {
  const categories = ref([])
  const tags = ref([])
  const loading = ref(false)

  const fetchCategories = async () => {
    loading.value = true
    try {
      const res = await getCategoryList()
      categories.value = res.data || []
    } finally {
      loading.value = false
    }
  }

  const fetchTags = async () => {
    loading.value = true
    try {
      const res = await getTagList()
      tags.value = res.data || []
    } finally {
      loading.value = false
    }
  }

  const addCategory = async (data) => {
    await createCategory(data)
    await fetchCategories()
  }

  const editCategory = async (data) => {
    await updateCategory(data)
    await fetchCategories()
  }

  const removeCategories = async (ids) => {
    await deleteCategories(ids)
    await fetchCategories()
  }

  const addTag = async (data) => {
    await createTag(data)
    await fetchTags()
  }

  const editTag = async (data) => {
    await updateTag(data)
    await fetchTags()
  }

  const removeTags = async (ids) => {
    await deleteTags(ids)
    await fetchTags()
  }

  /** 新建或更新分类（有 id 则更新，无则新建） */
  const saveCategory = (data) =>
    data.id ? editCategory(data) : addCategory(data)

  /** 新建或更新标签（有 id 则更新，无则新建） */
  const saveTag = (data) => (data.id ? editTag(data) : addTag(data))

  return {
    categories,
    tags,
    loading,
    fetchCategories,
    fetchTags,
    saveCategory,
    removeCategories,
    saveTag,
    removeTags
  }
})
