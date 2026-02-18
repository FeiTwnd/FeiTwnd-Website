import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getArticlePage,
  getArticleById,
  createArticle,
  updateArticle,
  deleteArticles,
  togglePublish
} from '@/api/article'
import { getCategoryList, getTagList } from '@/api/category'

export const useArticleStore = defineStore('article', () => {
  const list = ref([])
  const total = ref(0)
  const loading = ref(false)
  const categories = ref([])
  const tags = ref([])

  /**
   * 分页拉取文章列表
   * @param {{ page: number, pageSize: number, title?: string, categoryId?: number, isPublished?: number }} query
   */
  const fetchList = async (query) => {
    loading.value = true
    try {
      const res = await getArticlePage(query)
      list.value = res.data?.records || []
      total.value = res.data?.total || 0
    } finally {
      loading.value = false
    }
  }

  /** 拉取所有分类（用于下拉选择） */
  const fetchCategories = async () => {
    const res = await getCategoryList()
    categories.value = res.data || []
  }

  /** 拉取所有标签 */
  const fetchTags = async () => {
    const res = await getTagList()
    tags.value = res.data || []
  }

  /**
   * 获取文章详情
   * @param {number} id
   */
  const fetchDetail = async (id) => {
    const res = await getArticleById(id)
    return res.data
  }

  /**
   * 保存文章（创建或更新）
   * @param {Object} data
   */
  const saveArticle = async (data) => {
    if (data.id) {
      await updateArticle(data)
    } else {
      await createArticle(data)
    }
  }

  /**
   * 批量删除
   * @param {number[]} ids
   */
  const removeArticles = async (ids) => {
    await deleteArticles(ids)
  }

  /**
   * 切换发布状态
   * @param {number} id
   * @param {0|1} isPublished
   */
  const toggleArticlePublish = async (id, isPublished) => {
    await togglePublish(id, isPublished)
  }

  return {
    list,
    total,
    loading,
    categories,
    tags,
    fetchList,
    fetchCategories,
    fetchTags,
    fetchDetail,
    saveArticle,
    removeArticles,
    toggleArticlePublish
  }
})
