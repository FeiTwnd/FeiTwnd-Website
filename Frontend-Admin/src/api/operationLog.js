import http from '@/utils/request'

/**
 * 分页查询操作日志
 * @param {{ page: number, pageSize: number, operationTarget?: string, operationType?: string }} params
 */
export const getOperationLogPage = (params) =>
  http.get('/admin/operationLog/page', { params })

/**
 * 批量删除操作日志
 * @param {number[]} ids
 */
export const deleteOperationLogs = (ids) =>
  http.delete('/admin/operationLog', { params: { ids: ids.join(',') } })
