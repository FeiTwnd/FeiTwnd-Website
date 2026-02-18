import http from '@/utils/request'

/**
 * 总览数据（总浏览/访客/文章/评论等）
 * @returns {Promise<AdminOverviewVO>}
 */
export const getOverview = () => http.get('/admin/report/overview')

/**
 * 浏览量趋势统计
 * @param {{ begin: string, end: string }} params  yyyy-MM-dd 格式
 */
export const getViewStatistics = (params) =>
  http.get('/admin/report/viewStatistics', { params })

/**
 * 访客趋势统计
 * @param {{ begin: string, end: string }} params
 */
export const getVisitorStatistics = (params) =>
  http.get('/admin/report/visitorStatistics', { params })

/**
 * 访客省份分布
 */
export const getProvinceDistribution = () =>
  http.get('/admin/report/provinceDistribution')

/**
 * 文章访问量 TOP 10
 */
export const getArticleViewTop10 = () =>
  http.get('/admin/report/articleViewTop10')
