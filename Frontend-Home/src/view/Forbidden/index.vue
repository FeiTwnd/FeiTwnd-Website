<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  document.title = '403 - 访问受限'
})
</script>

<template>
  <div class="error-page">
    <div class="error-content">
      <h1 class="error-code">403</h1>
      <p class="error-title">访问受限</p>
      <p class="error-desc">你的访问已被限制，如有疑问请联系站长</p>
    </div>
  </div>
</template>

<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--blog-bg, #fafafa);
  font-family: var(
    --blog-sans,
    -apple-system,
    BlinkMacSystemFont,
    'Segoe UI',
    Roboto,
    sans-serif
  );
}
.error-content {
  text-align: center;
  padding: 40px 24px;
}
.error-code {
  font-size: 120px;
  font-weight: 900;
  color: var(--blog-text, #303133);
  margin: 0;
  line-height: 1;
  letter-spacing: -4px;
}
.error-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--blog-text, #303133);
  margin: 16px 0 8px;
}
.error-desc {
  font-size: 14px;
  color: var(--blog-text3, #909399);
  margin: 0 0 32px;
}

@media (max-width: 480px) {
  .error-code {
    font-size: 80px;
  }
  .error-title {
    font-size: 17px;
  }
}
</style>
