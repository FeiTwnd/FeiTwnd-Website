import request from '@/utils/request'

/** 获取所有可见文章分类 */
export const getCategories = () =>
  request.get('/blog/articleCategory')
