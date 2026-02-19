import request from '@/utils/request'

/** 获取个人信息 */
export const getPersonalInfo = () => request.get('/cv/personalInfo')
