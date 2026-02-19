import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getPersonalInfo } from '@/api/personalInfo'
import { getBlogReport } from '@/api/report'
import { getCategories } from '@/api/category'
import { getTags } from '@/api/tag'
import { getMusicList } from '@/api/music'

export const useBlogStore = defineStore('blog', () => {
  const personalInfo = ref({})
  const report = ref({})
  const categories = ref([])
  const tags = ref([])
  const musics = ref([])
  const loaded = ref(false)

  /* 通过 slug 反查分类 id */
  const getCategoryIdBySlug = (slug) => {
    const cat = categories.value.find((c) => c.slug === slug)
    return cat ? cat.id : null
  }

  /* 通过 slug 反查标签 id */
  const getTagIdBySlug = (slug) => {
    const tag = tags.value.find((t) => t.slug === slug)
    return tag ? tag.id : null
  }

  /* 通过 slug 取分类名 */
  const getCategoryNameBySlug = (slug) => {
    const cat = categories.value.find((c) => c.slug === slug)
    return cat ? cat.name : ''
  }

  /* 通过 slug 取标签名 */
  const getTagNameBySlug = (slug) => {
    const tag = tags.value.find((t) => t.slug === slug)
    return tag ? tag.name : ''
  }

  const init = async () => {
    if (loaded.value) return
    try {
      const [infoRes, reportRes, catRes, tagRes, musicRes] = await Promise.all([
        getPersonalInfo(),
        getBlogReport(),
        getCategories(),
        getTags(),
        getMusicList()
      ])
      personalInfo.value = infoRes.data.data ?? {}
      report.value = reportRes.data.data ?? {}
      categories.value = catRes.data.data ?? []
      tags.value = tagRes.data.data ?? []
      musics.value = musicRes.data.data ?? []
      loaded.value = true
    } catch (e) {
      console.error('博客数据加载失败', e)
    }
  }

  return {
    personalInfo,
    report,
    categories,
    tags,
    musics,
    loaded,
    init,
    getCategoryIdBySlug,
    getTagIdBySlug,
    getCategoryNameBySlug,
    getTagNameBySlug
  }
})
