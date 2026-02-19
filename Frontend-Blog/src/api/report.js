import request from '@/utils/request'

/** 获取博客统计数据 */
export const getBlogReport = () => request.get('/blog/report')
