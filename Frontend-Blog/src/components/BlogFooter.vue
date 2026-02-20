<script setup>
</script>

<template>
  <footer class="site-footer">
    <div class="footer-bottom">
      <span>
        &copy; {{ new Date().getFullYear() }} FeiTwnd. All rights reserved.
      </span>
    </div>
  </footer>
</template>

<style scoped>
.site-footer {
  background: #303133;
  color: rgba(255, 255, 255, 0.85);
  margin-top: auto;
}
.footer-bottom {
  text-align: center;
  padding: 14px 24px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}
</style>
