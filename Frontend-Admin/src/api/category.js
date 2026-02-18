import http from '@/utils/request'

/** 获取所有文章分类 */
export const getCategoryList = () => http.get('/admin/articleCategory')

/**
 * 新增分类
 * @param {{ name: string, slug: string, icon?: string, sort?: number }} data
 */
export const createCategory = (data) =>
  http.post('/admin/articleCategory', data)

/**
 * 更新分类
 * @param {{ id: number, name: string, slug: string, icon?: string, sort?: number }} data
 */
export const updateCategory = (data) => http.put('/admin/articleCategory', data)

/**
 * 批量删除分类
 * @param {number[]} ids
 */
export const deleteCategories = (ids) =>
  http.delete('/admin/articleCategory', { params: { ids: ids.join(',') } })

/** 获取所有标签 */
export const getTagList = () => http.get('/admin/article/tag')

/**
 * 新增标签
 * @param {{ name: string, slug: string }} data
 */
export const createTag = (data) => http.post('/admin/article/tag', data)

/**
 * 更新标签
 * @param {{ id: number, name: string, slug: string }} data
 */
export const updateTag = (data) => http.put('/admin/article/tag', data)

/**
 * 批量删除标签
 * @param {number[]} ids
 */
export const deleteTags = (ids) =>
  http.delete('/admin/article/tag', { params: { ids: ids.join(',') } })
