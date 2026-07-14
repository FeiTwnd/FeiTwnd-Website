import request from '@/utils/request'

/** 获取可见城市足迹 */
export const getVisibleFootprints = () => request.get('/blog/footprint')

/** 获取城市图片 */
export const getCityImages = (cityId) =>
  request.get('/blog/footprint/image', { params: { cityId } })
