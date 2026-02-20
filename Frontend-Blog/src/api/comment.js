import request from '@/utils/request'

/** 根据文章ID获取评论树（含当前访客的未审核评论） */
export const getCommentTree = (articleId, visitorId) =>
  request.get(`/blog/articleComment/article/${articleId}`, {
    params: { visitorId }
  })

/** 提交评论 */
export const submitComment = (data) =>
  request.post('/blog/articleComment', data)

/** 编辑评论 */
export const editComment = (data) =>
  request.put('/blog/articleComment/edit', data)

/** 删除评论 */
export const deleteComment = (id, visitorId) =>
  request.delete(`/blog/articleComment/${id}`, { params: { visitorId } })
