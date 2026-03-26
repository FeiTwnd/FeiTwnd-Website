import request from '@/utils/request'

/** 获取留言树（含当前访客的未审核留言） */
export const getMessageTree = (visitorId) =>
  request.get('/blog/message', { params: { visitorId } })

/** 提交留言 */
export const submitMessage = (data) => request.post('/blog/message', data)

/** 编辑留言 */
export const editMessage = (data, visitorToken, visitorFingerprint) =>
  request.put('/blog/message/edit', data, {
    headers: {
      'X-Visitor-Token': visitorToken || '',
      'X-Visitor-Fingerprint': visitorFingerprint || ''
    }
  })

/** 删除留言 */
export const deleteMessage = (id, visitorToken, visitorFingerprint) =>
  request.delete(`/blog/message/${id}`, {
    headers: {
      'X-Visitor-Token': visitorToken || '',
      'X-Visitor-Fingerprint': visitorFingerprint || ''
    }
  })
