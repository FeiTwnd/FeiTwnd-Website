import request from '@/utils/request'

/** 添加 RSS 订阅 */
export const addSubscription = (data) =>
  request.post('/blog/rssSubscription', data)

/** 取消 RSS 订阅（按邮箱） */
export const unsubscribe = (email) =>
  request.put('/blog/rssSubscription/unsubscribe', null, { params: { email } })

/** 检查是否已订阅 */
export const checkSubscription = (visitorId) =>
  request.get('/blog/rssSubscription/check', { params: { visitorId } })
