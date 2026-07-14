import http from '@/utils/request'

/**
 * 分页查询城市足迹
 * @param {{ page: number, pageSize: number, cityName?: string }} params
 */
export const getFootprintList = (params) =>
  http.get('/admin/footprint', { params })

/**
 * 新增城市足迹
 * @param {{ cityCode: string, cityName: string, visitTime: string, isVisible: number }} data
 */
export const createFootprint = (data) => http.post('/admin/footprint', data)

/**
 * 更新城市足迹
 * @param {{ id: number, cityCode: string, cityName: string, visitTime: string, isVisible: number }} data
 */
export const updateFootprint = (data) => http.put('/admin/footprint', data)

/**
 * 批量删除城市足迹
 * @param {number[]} ids
 */
export const deleteFootprints = (ids) =>
  http.delete('/admin/footprint', { params: { ids: ids.join(',') } })

/** 获取某城市的图片列表 */
export const getCityImages = (cityId) =>
  http.get('/admin/footprint/image', { params: { cityId } })

/**
 * 新增城市图片
 * @param {{ cityId: number, imageUrl: string, sort: number, isVisible: number }} data
 */
export const createCityImage = (data) => http.post('/admin/footprint/image', data)

/**
 * 更新城市图片
 * @param {{ id: number, cityId: number, imageUrl: string, sort: number, isVisible: number }} data
 */
export const updateCityImage = (data) => http.put('/admin/footprint/image', data)

/**
 * 删除单张图片
 * @param {number} id
 */
export const deleteCityImage = (id) =>
  http.delete('/admin/footprint/image', { params: { id } })
