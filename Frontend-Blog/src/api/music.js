import request from '@/utils/request'

/** 获取所有可见音乐 */
export const getMusicList = () =>
  request.get('/blog/music')
