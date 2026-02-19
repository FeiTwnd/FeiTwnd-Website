import request from '@/utils/request'

/** 记录访客访问信息 */
export const recordVisitor = (data) =>
  request.post('/blog/visitor/record', data)
