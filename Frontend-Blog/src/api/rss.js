import request from '@/utils/request'

/** 添加 RSS 订阅（需token验证） */
export const addSubscription = (data, visitorToken, visitorFingerprint) =>
  request.post('/blog/rssSubscription', data, {
    headers: {
      'X-Visitor-Token': visitorToken || '',
      'X-Visitor-Fingerprint': visitorFingerprint || ''
    }
  })

/** 取消 RSS 订阅（需token验证） */
export const unsubscribe = (visitorToken, visitorFingerprint) =>
  request.put('/blog/rssSubscription/unsubscribe', null, {
    headers: {
      'X-Visitor-Token': visitorToken || '',
      'X-Visitor-Fingerprint': visitorFingerprint || ''
    }
  })

/** 检查是否已订阅（需token验证） */
export const checkSubscription = (visitorToken, visitorFingerprint) =>
  request.get('/blog/rssSubscription/check', {
    headers: {
      'X-Visitor-Token': visitorToken || '',
      'X-Visitor-Fingerprint': visitorFingerprint || ''
    }
  })
