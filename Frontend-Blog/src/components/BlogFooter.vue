<script setup>
import { ref, onMounted } from 'vue'
import { getConfigByKey } from '@/api/systemConfig'

const icpBeian = ref('')
const gonganBeian = ref('')
const startYear = ref('')
const currentYear = new Date().getFullYear()

const apiBase = import.meta.env.DEV ? '/api' : ''
const sitemapUrl = `${apiBase}/blog/sitemap.xml`
const rssFeedUrl = `${apiBase}/blog/rss`

onMounted(async () => {
  try {
    const [icpRes, gonganRes, startRes] = await Promise.all([
      getConfigByKey('icp-beian').catch(() => null),
      getConfigByKey('gongan-beian').catch(() => null),
      getConfigByKey('start-time').catch(() => null)
    ])
    icpBeian.value = icpRes?.data?.data?.configValue || ''
    gonganBeian.value = gonganRes?.data?.data?.configValue || ''
    const sv = startRes?.data?.data?.configValue || ''
    startYear.value = sv ? sv.split('-')[0] : ''
  } catch {
    /* ignore */
  }
})
</script>

<template>
  <footer class="site-footer">
    <div class="footer-inner">
      <div v-if="gonganBeian || icpBeian" class="footer-beian">
        <span v-if="gonganBeian">{{ gonganBeian }}</span>
        <span v-if="gonganBeian && icpBeian" class="divider">|</span>
        <a
          v-if="icpBeian"
          href="https://beian.miit.gov.cn/"
          target="_blank"
          rel="noopener noreferrer"
          >{{ icpBeian }}</a
        >
      </div>
      <div class="footer-copy">
        &copy; {{ startYear ? `${startYear}-` : '' }}{{ currentYear }} FeiTwnd.
        All rights reserved.
      </div>
      <div class="footer-links">
        <a :href="sitemapUrl" target="_blank" rel="noopener">Sitemap</a>
        <span class="divider">|</span>
        <a :href="rssFeedUrl" target="_blank" rel="noopener">RSS</a>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.site-footer {
  background: #303133;
  color: rgba(255, 255, 255, 0.85);
  margin-top: auto;
}
.footer-inner {
  text-align: center;
  padding: 14px 24px;
}
.footer-beian {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 4px;
}
.footer-beian a {
  color: rgba(255, 255, 255, 0.5);
  text-decoration: none;
  border-bottom: 1px dotted rgba(255, 255, 255, 0.3);
  transition: color 0.2s;
}
.footer-beian a:hover {
  color: rgba(255, 255, 255, 0.8);
}
.divider {
  margin: 0 8px;
  opacity: 0.4;
}
.footer-copy {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}
.footer-links {
  font-size: 12px;
  margin-top: 4px;
  color: rgba(255, 255, 255, 0.35);
}
.footer-links a {
  color: rgba(255, 255, 255, 0.45);
  text-decoration: none;
  border-bottom: 1px dotted rgba(255, 255, 255, 0.25);
  transition: color 0.2s;
}
.footer-links a:hover {
  color: rgba(255, 255, 255, 0.8);
}
</style>
