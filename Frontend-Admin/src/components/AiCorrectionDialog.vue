<script setup>
import { ref, watch, computed } from 'vue'
import { diffLines } from 'diff'
import { correctTypo } from '@/api/ai'

const props = defineProps({
  // 弹窗可见性
  modelValue: { type: Boolean, default: false },
  // 当前文章 Markdown 内容
  content: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue', 'applied'])

// 请求中：显示笔动画
const loading = ref(false)
// 是否已完成纠错（区分加载态与结果态）
const done = ref(false)
// 错误信息（请求失败时展示）
const errorMsg = ref('')

// diff 块：上下文行(灰) + 变更块(红绿+勾选)
// 变更块结构: { oldLines: [], newLines: [], accepted: true }
const diffItems = ref([])

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const hasChanges = computed(() =>
  diffItems.value.some((item) => item.type === 'change')
)

const acceptedCount = computed(
  () =>
    diffItems.value.filter((item) => item.type === 'change' && item.accepted)
      .length
)

/**
 * 弹窗打开时自动请求 AI 纠错
 */
watch(
  () => props.modelValue,
  async (open) => {
    if (!open) return
    loading.value = true
    done.value = false
    errorMsg.value = ''
    diffItems.value = []
    try {
      const corrected = await correctTypo(props.content)
      if (!corrected) {
        errorMsg.value = 'AI 返回结果为空，请重试'
        return
      }
      buildDiff(props.content, corrected)
      done.value = true
    } catch (e) {
      errorMsg.value = e?.response?.data?.msg || 'AI 纠错请求失败，请稍后重试'
    } finally {
      loading.value = false
    }
  }
)

/**
 * 使用 diff 库逐行对比原文与纠错结果，生成"上下文 + 变更块"列表
 * 变更块统一语义：勾选 = 采纳 AI（删除原行/插入新行/替换），不勾 = 保留原文
 */
const buildDiff = (oldText, newText) => {
  const parts = diffLines(oldText, newText)
  const items = []

  parts.forEach((part) => {
    const lines = part.value.split('\n')
    // 去掉末尾由换行符产生的空串，保留中间的空行
    if (lines.length > 0 && lines[lines.length - 1] === '') lines.pop()

    if (part.removed) {
      // 删除行：并入前一个变更块（若无则新建），newLines 留空
      const last = items[items.length - 1]
      if (last && last.type === 'change' && last.newLines.length === 0) {
        last.oldLines.push(...lines)
      } else {
        items.push({
          type: 'change',
          oldLines: [...lines],
          newLines: [],
          accepted: true
        })
      }
    } else if (part.added) {
      // 新增行：若前一个是删除块则合并为替换块，否则新建新增块
      const last = items[items.length - 1]
      if (
        last &&
        last.type === 'change' &&
        last.oldLines.length > 0 &&
        last.newLines.length === 0
      ) {
        last.newLines.push(...lines)
      } else {
        items.push({
          type: 'change',
          oldLines: [],
          newLines: [...lines],
          accepted: true
        })
      }
    } else {
      lines.forEach((line) => items.push({ type: 'context', line }))
    }
  })
  diffItems.value = items
}

/**
 * 确认应用：按勾选结果重建完整内容并回传
 */
const applyChanges = () => {
  const out = []
  diffItems.value.forEach((item) => {
    if (item.type === 'context') {
      out.push(item.line)
    } else {
      // 勾选 = 采纳 AI（newLines），不勾 = 保留原文（oldLines）
      out.push(...(item.accepted ? item.newLines : item.oldLines))
    }
  })
  emit('applied', out.join('\n'))
  visible.value = false
}

const close = () => {
  visible.value = false
}
</script>

<template>
  <el-dialog
    v-model="visible"
    title="AI 错别字 / 病句纠错"
    width="78%"
    top="4vh"
    class="ai-correct-dialog"
    :close-on-click-modal="false"
    @closed="diffItems = []"
  >
    <!-- 等待动画：笔从左到右画曲线 -->
    <div v-if="loading" class="ai-loading">
      <svg
        class="ai-loading-svg"
        viewBox="0 0 260 56"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          id="ai-wave"
          d="M8 38 Q 22 12 36 38 T 64 38 T 92 38 T 120 38 T 148 38 T 176 38 T 204 38 T 232 38"
          fill="none"
          stroke="#909399"
          stroke-width="2"
          stroke-linecap="round"
          class="ai-wave-line"
        />
        <g>
          <animateMotion
            dur="2.2s"
            repeatCount="indefinite"
            rotate="auto"
            keyPoints="0;1"
            keyTimes="0;1"
          >
            <mpath href="#ai-wave" />
          </animateMotion>
          <rect x="-5" y="-1.8" width="10" height="3.6" rx="1" fill="#000" />
          <polygon points="5,-1.8 10,0 5,1.8" fill="#f56c6c" />
          <rect x="-5" y="-3.4" width="3" height="6.8" rx="1" fill="#c0c4cc" />
        </g>
      </svg>
      <p class="ai-loading-text">AI 正在纠错，请稍候…</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="errorMsg" class="ai-error">
      <p class="ai-error-text">{{ errorMsg }}</p>
      <el-button size="small" @click="close">关闭</el-button>
    </div>

    <!-- 结果：diff 对比 -->
    <div v-else-if="done" class="ai-diff">
      <div class="ai-diff-summary">
        <span class="ai-diff-hint"
          >勾选 = 采纳 AI 修改（删除原行 / 插入新行 / 替换），不勾选 =
          保留原文</span
        >
        <span v-if="acceptedCount" class="ai-diff-count"
          >已采纳 {{ acceptedCount }} 处</span
        >
      </div>

      <div v-if="!hasChanges" class="ai-diff-empty">
        <span class="iconfont icon-success" />
        <p>未发现错别字或病句，内容无需修改</p>
      </div>

      <div v-else class="ai-diff-list">
        <template v-for="(item, idx) in diffItems" :key="idx">
          <!-- 上下文行：弱化显示 -->
          <div v-if="item.type === 'context'" class="diff-row context-row">
            <span class="diff-marker" />
            <pre class="diff-line context-line">{{ item.line || ' ' }}</pre>
          </div>

          <!-- 变更块：勾选框 + 红(旧)/绿(新) 对比 -->
          <div v-else class="diff-change">
            <el-checkbox v-model="item.accepted" class="diff-check" />
            <div class="diff-change-body">
              <div
                v-for="(oldLine, oi) in item.oldLines"
                :key="'o' + oi"
                class="diff-row old-row"
              >
                <span class="diff-marker">-</span>
                <pre class="diff-line old-line">{{ oldLine || ' ' }}</pre>
              </div>
              <div
                v-for="(newLine, ni) in item.newLines"
                :key="'n' + ni"
                class="diff-row new-row"
              >
                <span class="diff-marker">+</span>
                <pre class="diff-line new-line">{{ newLine || ' ' }}</pre>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>

    <template #footer>
      <el-button size="small" @click="close">取消</el-button>
      <el-button
        v-if="done && hasChanges"
        size="small"
        type="primary"
        @click="applyChanges"
        >应用修改</el-button
      >
    </template>
  </el-dialog>
</template>

<style scoped>
.ai-correct-dialog :deep(.el-dialog__body) {
  padding: 12px 20px 20px;
  min-height: 320px;
  max-height: 72vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* ---- 等待动画 ---- */
.ai-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  min-height: 260px;
  gap: 16px;
}
.ai-loading-svg {
  width: 300px;
  height: 60px;
}
.ai-wave-line {
  stroke-dasharray: 260;
  stroke-dashoffset: 260;
  animation: ai-draw 2.2s linear infinite;
}
@keyframes ai-draw {
  0% {
    stroke-dashoffset: 260;
  }
  45% {
    stroke-dashoffset: 0;
  }
  55% {
    stroke-dashoffset: 0;
  }
  100% {
    stroke-dashoffset: 260;
  }
}
.ai-loading-text {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

/* ---- 错误 ---- */
.ai-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  min-height: 260px;
  gap: 16px;
}
.ai-error-text {
  margin: 0;
  font-size: 14px;
  color: #f56c6c;
}

/* ---- diff ---- */
.ai-diff {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}
.ai-diff-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 2px 10px;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}
.ai-diff-hint {
  font-size: 12px;
  color: #909399;
}
.ai-diff-count {
  font-size: 12px;
  color: #303133;
  font-weight: 600;
}
.ai-diff-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  min-height: 200px;
  gap: 10px;
  color: #67c23a;
  font-size: 14px;
}
.ai-diff-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  font-size: 13px;
  line-height: 1.6;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  background: #fff;
}

/* 普通行 */
.diff-row {
  display: flex;
  align-items: flex-start;
  min-height: 24px;
}
.diff-marker {
  width: 24px;
  flex-shrink: 0;
  text-align: center;
  user-select: none;
  font-weight: 700;
}
.diff-line {
  margin: 0;
  flex: 1;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: inherit;
  font-size: inherit;
  line-height: inherit;
}

/* 上下文行 */
.context-row {
  background: #fff;
  color: #b0b3b8;
}
.context-line {
  color: #909399;
}

/* 变更块 */
.diff-change {
  display: flex;
  align-items: flex-start;
  background: #fafafa;
}
.diff-check {
  margin: 6px 6px 0 6px;
  flex-shrink: 0;
}
.diff-change-body {
  flex: 1;
  min-width: 0;
}

/* 删除行（红） */
.old-row {
  background: #fef0f0;
  color: #f56c6c;
}
.old-row .diff-marker {
  color: #f56c6c;
}
.old-line {
  color: #c45656;
  text-decoration: line-through;
  text-decoration-color: rgba(245, 108, 108, 0.45);
}

/* 新增行（绿） */
.new-row {
  background: #f0f9eb;
  color: #67c23a;
}
.new-row .diff-marker {
  color: #67c23a;
}
.new-line {
  color: #529b2e;
}
</style>
