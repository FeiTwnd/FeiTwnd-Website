import { defineStore } from 'pinia'
import { ref } from 'vue'
import { recordVisitor } from '@/api/visitor'
import { collectFingerprint } from '@/utils/fingerprint'

export const useVisitorStore = defineStore(
  'visitor',
  () => {
    const visitorId = ref(null)
    const sessionId = ref('')
    const fingerprint = ref('')
    const visitorToken = ref('')
    const nickname = ref('')
    const email = ref('')

    const record = async () => {
      try {
        const fp = collectFingerprint()
        const res = await recordVisitor(fp)
        const d = res.data.data
        visitorId.value = d.visitorId
        sessionId.value = d.sessionId
        fingerprint.value = d.visitorFingerprint
        visitorToken.value = d.visitorToken || ''
      } catch (e) {
        console.error('访客记录失败', e)
      }
    }

    return {
      visitorId,
      sessionId,
      fingerprint,
      visitorToken,
      nickname,
      email,
      record
    }
  },
  {
    persist: {
      pick: ['visitorId', 'fingerprint', 'visitorToken', 'nickname', 'email']
    }
  }
)
