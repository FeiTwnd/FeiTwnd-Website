import request from '@/utils/request'

/** 生成算术验证码 */
export const generateCaptcha = () =>
  request.get('/blog/common/captcha/generate')
