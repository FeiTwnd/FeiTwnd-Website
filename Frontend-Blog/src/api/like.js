import request from '@/utils/request'

/** 点赞文章（需token验证） */
export const likeArticle = (articleId, visitorToken, visitorFingerprint) =>
  request.post(`/blog/articleLike/${articleId}`, null, {
    headers: {
      'X-Visitor-Token': visitorToken || '',
      'X-Visitor-Fingerprint': visitorFingerprint || ''
    }
  })

/** 取消点赞（需token验证） */
export const unlikeArticle = (articleId, visitorToken, visitorFingerprint) =>
  request.delete(`/blog/articleLike/${articleId}`, {
    headers: {
      'X-Visitor-Token': visitorToken || '',
      'X-Visitor-Fingerprint': visitorFingerprint || ''
    }
  })

/** 检查是否已点赞（需token验证） */
export const hasLiked = (articleId, visitorToken, visitorFingerprint) =>
  request.get(`/blog/articleLike/${articleId}`, {
    headers: {
      'X-Visitor-Token': visitorToken || '',
      'X-Visitor-Fingerprint': visitorFingerprint || ''
    }
  })
