import request from '@/utils/request'

/** 获取留言树 */
export const getMessageTree = () => request.get('/blog/message')

/** 提交留言 */
export const submitMessage = (data) => request.post('/blog/message', data)

/** 编辑留言 */
export const editMessage = (data) => request.put('/blog/message/edit', data)

/** 删除留言 */
export const deleteMessage = (id, visitorId) =>
  request.delete(`/blog/message/${id}`, { params: { visitorId } })
