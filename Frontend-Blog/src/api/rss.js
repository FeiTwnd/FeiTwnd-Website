import request from '@/utils/request'

/** 添加 RSS 订阅 */
export const addSubscription = (data) =>
  request.post('/blog/rssSubscription', data)

/** 取消 RSS 订阅 */
export const unsubscribe = (email) =>
  request.put('/blog/rssSubscription/unsubscribe', null, { params: { email } })
