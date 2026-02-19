/**
 * 收集浏览器指纹信息用于访客记录
 */
export const collectFingerprint = () => ({
  pagePath: window.location.pathname,
  pageTitle: document.title,
  referer: document.referrer || '',
  screen: `${window.screen.width}x${window.screen.height}`,
  timezone: Intl.DateTimeFormat().resolvedOptions().timeZone || '',
  language: navigator.language || '',
  platform: navigator.platform || navigator.userAgentData?.platform || '',
  cookiesEnabled: navigator.cookieEnabled,
  deviceMemory: navigator.deviceMemory || null,
  hardwareConcurrency: navigator.hardwareConcurrency || null
})
