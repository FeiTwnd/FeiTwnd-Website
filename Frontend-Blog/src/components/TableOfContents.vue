<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue'

const props = defineProps({
  contentHtml: { type: String, default: '' }
})

const headings = ref([])
const activeId = ref('')

const getRootLevel = (list) => {
  const levels = list.map((h) => h.level)
  if (levels.includes(2)) return 2
  return Math.min(...levels)
}

const buildHeadingTreeMeta = (list) => {
  if (!list.length) return []
  const rootLevel = getRootLevel(list)
  let currentRootId = ''
  return list.map((h) => {
    if (h.level === rootLevel) {
      currentRootId = h.id
    }
    return {
      ...h,
      isRoot: h.level === rootLevel,
      rootId: currentRootId || h.id
    }
  })
}

const activeRootId = computed(() => {
  const active = headings.value.find((h) => h.id === activeId.value)
  if (active) return active.rootId
  return headings.value.find((h) => h.isRoot)?.id || ''
})

const visibleHeadings = computed(() => {
  const rootId = activeRootId.value
  return headings.value.filter((h) => h.isRoot || h.rootId === rootId)
})

const extractHeadings = () => {
  nextTick(() => {
    const el = document.querySelector('.article-content')
    if (!el) return
    const nodes = el.querySelectorAll('h1, h2, h3, h4')
    const list = []
    nodes.forEach((node, idx) => {
      const id = node.id || `heading-${idx}`
      node.id = id
      list.push({
        id,
        text: node.textContent.trim(),
        level: parseInt(node.tagName.charAt(1))
      })
    })
    headings.value = buildHeadingTreeMeta(list)
    if (headings.value.length && !activeId.value) {
      activeId.value = headings.value[0].id
    }
    handleScroll()
  })
}

const handleScroll = () => {
  const offset = 100
  for (let i = headings.value.length - 1; i >= 0; i--) {
    const el = document.getElementById(headings.value[i].id)
    if (el && el.getBoundingClientRect().top <= offset) {
      activeId.value = headings.value[i].id
      return
    }
  }
  if (headings.value.length) activeId.value = headings.value[0].id
}

const scrollTo = (id) => {
  const el = document.getElementById(id)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

watch(() => props.contentHtml, extractHeadings)
onMounted(() => {
  extractHeadings()
  window.addEventListener('scroll', handleScroll, { passive: true })
})
onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<template>
  <div v-if="headings.length" class="toc-card side-card">
    <h4 class="toc-title"><i class="iconfont icon-guidang" /> 目录</h4>
    <TransitionGroup name="toc-slide" tag="ul" class="toc-list">
      <li
        v-for="h in visibleHeadings"
        :key="h.id"
        class="toc-item"
        :class="{
          active: activeId === h.id,
          root: h.isRoot,
          [`level-${h.level}`]: true
        }"
        @click="scrollTo(h.id)"
      >
        {{ h.text }}
      </li>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toc-card {
  background: var(--blog-card, #fff);
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--blog-border-light, #ebeef5);
  padding: 16px;
  margin-bottom: 16px;
}
.toc-title {
  font-size: 14px;
  font-weight: 700;
  margin: 0 0 10px;
  color: var(--blog-text, #303133);
  display: flex;
  align-items: center;
  gap: 6px;
}
.toc-title .iconfont {
  font-size: 15px;
}
.toc-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.toc-item {
  font-size: 13px;
  color: var(--blog-text2, #606266);
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  border-left: 2px solid transparent;
  transition:
    color 0.15s,
    background 0.15s,
    border-color 0.15s;
  line-height: 1.5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.toc-item:hover {
  color: var(--blog-text, #303133);
  background: var(--blog-hover, #f5f7fa);
}
.toc-item.active {
  color: var(--blog-text, #000);
  font-weight: 600;
  border-left-color: var(--blog-text, #303133);
  background: var(--blog-hover, #f5f7fa);
}
.toc-item.root {
  font-weight: 600;
}
.toc-item.level-2 {
  padding-left: 8px;
}
.toc-item.level-3 {
  padding-left: 20px;
  font-size: 12px;
}
.toc-item.level-4 {
  padding-left: 32px;
  font-size: 12px;
}

.toc-slide-enter-active,
.toc-slide-leave-active {
  transition:
    opacity 0.2s ease,
    transform 0.2s ease,
    max-height 0.22s ease,
    margin 0.22s ease,
    padding 0.22s ease;
}

.toc-slide-enter-from,
.toc-slide-leave-to {
  opacity: 0;
  transform: translateY(-4px);
  max-height: 0;
  margin-top: 0;
  margin-bottom: 0;
  padding-top: 0;
  padding-bottom: 0;
}

.toc-slide-enter-to,
.toc-slide-leave-from {
  opacity: 1;
  transform: translateY(0);
  max-height: 36px;
}

.toc-slide-move {
  transition: transform 0.2s ease;
}
</style>
