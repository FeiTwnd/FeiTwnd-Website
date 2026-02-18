import http from '@/utils/request'

/** 获取所有友情链接 */
export const getFriendLinkList = () => http.get('/admin/friendLink')

/**
 * 新增友情链接
 * @param {{ name: string, url: string, logo?: string, description?: string }} data
 */
export const createFriendLink = (data) => http.post('/admin/friendLink', data)

/**
 * 更新友情链接
 * @param {{ id: number, name: string, url: string, logo?: string, description?: string }} data
 */
export const updateFriendLink = (data) => http.put('/admin/friendLink', data)

/**
 * 批量删除友情链接
 * @param {number[]} ids
 */
export const deleteFriendLinks = (ids) =>
  http.delete('/admin/friendLink', { params: { ids: ids.join(',') } })
