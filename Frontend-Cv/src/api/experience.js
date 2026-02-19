import request from '@/utils/request'

/** 获取全部经历（教育 / 工作 / 项目） */
export const getExperiences = () => request.get('/cv/experience')
