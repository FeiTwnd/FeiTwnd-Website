import request from '@/utils/request'

/** 点赞文章 */
export const likeArticle = (articleId, visitorId) =>
  request.post(`/blog/articleLike/${articleId}`, null, {
    params: { visitorId }
  })

/** 取消点赞 */
export const unlikeArticle = (articleId, visitorId) =>
  request.delete(`/blog/articleLike/${articleId}`, { params: { visitorId } })

/** 检查是否已点赞 */
export const hasLiked = (articleId, visitorId) =>
  request.get(`/blog/articleLike/${articleId}`, { params: { visitorId } })
