import request from '@/utils/request'

/** 获取可见友情链接 */
export const getFriendLinks = () =>
  request.get('/blog/friendLink')
