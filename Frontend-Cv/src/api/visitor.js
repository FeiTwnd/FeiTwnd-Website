import request from '@/utils/request'

/** 收集浏览器指纹信息 */
const collectBrowserInfo = () => ({
  pagePath: window.location.pathname,
  pageTitle: document.title,
  referer: document.referrer || '',
  screen: `${window.screen.width}x${window.screen.height}`,
  timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
  language: navigator.language,
  platform: navigator.platform,
  cookiesEnabled: navigator.cookieEnabled,
  deviceMemory: navigator.deviceMemory || null,
  hardwareConcurrency: navigator.hardwareConcurrency || null
})

/** 记录访客 */
export const recordVisitor = () =>
  request.post('/cv/visitor/record', collectBrowserInfo())
