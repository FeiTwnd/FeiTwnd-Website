import request from '@/utils/request'

/** 获取博主个人信息 */
export const getPersonalInfo = () => request.get('/blog/personalInfo')
