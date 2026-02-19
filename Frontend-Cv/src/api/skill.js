import request from '@/utils/request'

/** 获取技能列表 */
export const getSkills = () => request.get('/cv/skill')
