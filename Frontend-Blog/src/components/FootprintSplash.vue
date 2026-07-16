<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  dark: { type: Boolean, default: false }
})

const phase = ref('playing')
const timers = []

onMounted(() => {
  timers.push(
    setTimeout(() => {
      phase.value = 'fading'
    }, 3000)
  )
  timers.push(
    setTimeout(() => {
      phase.value = 'done'
    }, 4500)
  )
})

onUnmounted(() => timers.forEach(clearTimeout))
</script>

<template>
  <div
    v-show="phase !== 'done'"
    class="footprint-splash"
    :class="{ dark: props.dark, fading: phase === 'fading' }"
  >
    <svg
      class="hiker-svg"
      viewBox="0 0 200 240"
      xmlns="http://www.w3.org/2000/svg"
    >
      <!-- 太阳 -->
      <g class="sun">
        <circle cx="170" cy="36" r="12" class="sun-core" />
        <g class="sun-rays">
          <line
            x1="170"
            y1="18"
            x2="170"
            y2="12"
            stroke-width="2"
            stroke-linecap="round"
          />
          <line
            x1="170"
            y1="54"
            x2="170"
            y2="60"
            stroke-width="2"
            stroke-linecap="round"
          />
          <line
            x1="152"
            y1="36"
            x2="146"
            y2="36"
            stroke-width="2"
            stroke-linecap="round"
          />
          <line
            x1="188"
            y1="36"
            x2="194"
            y2="36"
            stroke-width="2"
            stroke-linecap="round"
          />
          <line
            x1="157"
            y1="23"
            x2="153"
            y2="19"
            stroke-width="2"
            stroke-linecap="round"
          />
          <line
            x1="183"
            y1="49"
            x2="187"
            y2="53"
            stroke-width="2"
            stroke-linecap="round"
          />
          <line
            x1="183"
            y1="23"
            x2="187"
            y2="19"
            stroke-width="2"
            stroke-linecap="round"
          />
          <line
            x1="157"
            y1="49"
            x2="153"
            y2="53"
            stroke-width="2"
            stroke-linecap="round"
          />
        </g>
      </g>

      <!-- 飞鸟 -->
      <g class="birds">
        <path
          class="bird bird-1"
          d="M 30 38 Q 35 31 40 38 Q 45 31 50 38"
          fill="none"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
        <path
          class="bird bird-2"
          d="M 48 28 Q 52 23 56 28 Q 60 23 64 28"
          fill="none"
          stroke-width="1.5"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
        <path
          class="bird bird-3"
          d="M 20 48 Q 23 43 26 48 Q 29 43 32 48"
          fill="none"
          stroke-width="1.5"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </g>

      <!-- 远山（低透明度层次感） -->
      <g class="mountains-far">
        <path d="M-20 190 L20 130 L60 190 Z" />
        <path d="M40 190 L100 110 L160 190 Z" />
        <path d="M130 190 L170 130 L210 190 Z" />
      </g>

      <!-- 近山 -->
      <g class="mountains">
        <path d="M-30 180 L30 100 L90 180 Z" />
        <path d="M50 180 L120 75 L190 180 Z" />
        <path d="M150 180 L190 110 L230 180 Z" />
      </g>

      <!-- 松树 -->
      <g class="trees">
        <g class="tree tree-1">
          <rect x="29" y="162" width="4" height="10" rx="1" />
          <path d="M19 165 L31 148 L43 165 Z" />
          <path d="M21 157 L31 142 L41 157 Z" />
        </g>
        <g class="tree tree-2">
          <rect x="158" y="148" width="3" height="7" rx="1" />
          <path d="M151 150 L160 137 L169 150 Z" />
          <path d="M153 144 L160 133 L167 144 Z" />
        </g>
        <g class="tree tree-3">
          <rect x="140" y="160" width="4" height="9" rx="1" />
          <path d="M129 163 L142 145 L155 163 Z" />
          <path d="M131 155 L142 139 L153 155 Z" />
        </g>
      </g>

      <!-- 云朵 -->
      <g class="clouds">
        <g class="cloud cloud-1">
          <ellipse cx="45" cy="55" rx="16" ry="9" />
          <ellipse cx="60" cy="50" rx="12" ry="8" />
          <ellipse cx="30" cy="53" rx="10" ry="7" />
        </g>
        <g class="cloud cloud-2">
          <ellipse cx="160" cy="52" rx="14" ry="8" />
          <ellipse cx="172" cy="48" rx="10" ry="6" />
          <ellipse cx="148" cy="50" rx="9" ry="6" />
        </g>
      </g>

      <!-- 山坡基线 -->
      <path
        class="slope"
        d="M-20 220 L220 165"
        stroke-width="4"
        stroke-linecap="round"
      />

      <!-- 登山小径 -->
      <path
        class="trail"
        d="M-5 226 L25 213 L48 210 L68 204 L85 196 L100 190 L118 184 L140 178 L168 170"
      />

      <!-- 登山小人 -->
      <g class="hiker">
        <!-- 投影 -->
        <ellipse class="shadow" cx="100" cy="200" rx="28" ry="6" />

        <!-- 登山包 -->
        <g class="backpack">
          <rect x="76" y="96" width="26" height="44" rx="8" />
          <rect x="80" y="122" width="18" height="12" rx="3" />
        </g>

        <!-- 后腿 (左腿) -->
        <g class="limb leg back-leg">
          <path d="M104 160 L72 206" stroke-width="9" stroke-linecap="round" />
        </g>

        <!-- 后臂 (左臂) -->
        <g class="limb arm back-arm">
          <path d="M104 108 L72 144" stroke-width="8" stroke-linecap="round" />
        </g>

        <!-- 身体 -->
        <rect class="body" x="94" y="100" width="20" height="60" rx="10" />

        <!-- 头部与遮阳帽 -->
        <g class="head-group">
          <circle class="head" cx="104" cy="88" r="13" />
          <path
            class="hat"
            d="M82 83 C82 83 88 63 104 61 C120 63 126 83 126 83 C126 87 120 89 104 89 C88 89 82 87 82 83 Z"
          />
        </g>

        <!-- 前腿 (右腿) -->
        <g class="limb leg front-leg">
          <path d="M104 160 L136 206" stroke-width="9" stroke-linecap="round" />
        </g>

        <!-- 前臂 (右臂) -->
        <g class="limb arm front-arm">
          <path d="M104 108 L136 146" stroke-width="8" stroke-linecap="round" />
        </g>

        <!-- 登山杖 -->
        <g class="pole-group">
          <line
            class="pole"
            x1="136"
            y1="138"
            x2="150"
            y2="220"
            stroke-width="3"
            stroke-linecap="round"
          />
        </g>
      </g>
    </svg>
  </div>
</template>

<style scoped>
.footprint-splash {
  position: fixed;
  inset: 0;
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  opacity: 1;
  transition: opacity 1.5s ease;
  pointer-events: auto;
}
.footprint-splash.dark {
  background: #181818;
}
.footprint-splash.fading {
  opacity: 0;
  pointer-events: none;
}

.hiker-svg {
  width: 420px;
  height: 504px;
  overflow: visible;
}

/* ======== 太阳 ======== */
.sun-core {
  fill: #f0d080;
  animation: sun-glow 3s ease-in-out infinite;
  transform-origin: 170px 36px;
}
.sun-rays {
  stroke: #f0d080;
  opacity: 0.45;
  animation: sun-rays 3s ease-in-out infinite;
  transform-origin: 170px 36px;
}
@keyframes sun-glow {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.08);
  }
}
@keyframes sun-rays {
  0%,
  100% {
    transform: rotate(0deg);
    opacity: 0.45;
  }
  50% {
    transform: rotate(8deg);
    opacity: 0.65;
  }
}

/* ======== 飞鸟 ======== */
.birds {
  stroke: #909399;
}
.bird-1 {
  animation: bird-soar 5s ease-in-out infinite;
}
.bird-2 {
  animation: bird-soar 5s ease-in-out 0.6s infinite;
}
.bird-3 {
  animation: bird-soar 5s ease-in-out 1.2s infinite;
}
@keyframes bird-soar {
  0%,
  100% {
    transform: translateX(0) translateY(0);
  }
  25% {
    transform: translateX(8px) translateY(-5px);
  }
  50% {
    transform: translateX(16px) translateY(0);
  }
  75% {
    transform: translateX(8px) translateY(3px);
  }
}

/* ======== 山 ======== */
.mountains-far {
  fill: #eef1f6;
  animation: mountain-drift-far 16s ease-in-out infinite alternate;
  transform-origin: 100px 180px;
}
.mountains {
  fill: #e4e7ed;
  animation: mountain-drift 12s ease-in-out infinite alternate;
  transform-origin: 100px 180px;
}
@keyframes mountain-drift-far {
  0% {
    transform: translateX(-3px);
  }
  100% {
    transform: translateX(3px);
  }
}
@keyframes mountain-drift {
  0% {
    transform: translateX(-2px);
  }
  100% {
    transform: translateX(2px);
  }
}

/* ======== 松树 ======== */
.trees {
  fill: #dcdfe6;
}
.tree-1 {
  animation: tree-sway 5s ease-in-out infinite;
  transform-origin: 31px 172px;
}
.tree-2 {
  animation: tree-sway 5s ease-in-out 0.8s infinite;
  transform-origin: 160px 155px;
}
.tree-3 {
  animation: tree-sway 5s ease-in-out 1.6s infinite;
  transform-origin: 142px 169px;
}
@keyframes tree-sway {
  0%,
  100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(0.6deg);
  }
  75% {
    transform: rotate(-0.6deg);
  }
}

/* ======== 云 ======== */
.clouds {
  fill: #ebeef5;
}
.cloud {
  animation: cloud-float 8s ease-in-out infinite alternate;
}
.cloud-2 {
  animation-delay: -4s;
}
@keyframes cloud-float {
  0% {
    transform: translateX(-6px);
  }
  100% {
    transform: translateX(6px);
  }
}

/* ======== 山坡 & 小径 ======== */
.slope {
  stroke: #e4e7ed;
}
.trail {
  fill: none;
  stroke: #dcdfe6;
  stroke-width: 1.5;
  stroke-dasharray: 3 4;
  stroke-linecap: round;
  animation: trail-dash 2s linear infinite;
}
@keyframes trail-dash {
  0% {
    stroke-dashoffset: 0;
  }
  100% {
    stroke-dashoffset: -14;
  }
}

/* ======== 主体色彩 ======== */
.body,
.backpack,
.head,
.hat {
  fill: #303133;
}
.backpack rect:last-child {
  fill: rgba(48, 49, 51, 0.18);
}
.limb path,
.pole {
  stroke: #303133;
}
.limb path {
  fill: none;
}
.shadow {
  fill: rgba(48, 49, 51, 0.12);
}

/* ============ 登山小人动画 ============ */

.hiker {
  transform: rotate(-6deg);
  transform-origin: 100px 180px;
  animation: hiker-bounce 1.4s ease-in-out infinite;
}
@keyframes hiker-bounce {
  0%,
  100% {
    translate: 0 0;
  }
  15% {
    translate: 0 -7px;
  }
  50% {
    translate: 0 0;
  }
  65% {
    translate: 0 -7px;
  }
}

.shadow {
  animation: shadow-pulse 1.4s ease-in-out infinite;
  transform-origin: 100px 200px;
}
@keyframes shadow-pulse {
  0%,
  100% {
    transform: scale(1, 1);
    opacity: 1;
  }
  15% {
    transform: scale(0.78, 0.78);
    opacity: 0.55;
  }
  50% {
    transform: scale(1, 1);
    opacity: 1;
  }
  65% {
    transform: scale(0.78, 0.78);
    opacity: 0.55;
  }
}

.body {
  animation: torso-sway 1.4s ease-in-out infinite;
  transform-origin: 104px 160px;
}
@keyframes torso-sway {
  0%,
  100% {
    transform: rotate(1.2deg);
  }
  25% {
    transform: rotate(0deg);
  }
  50% {
    transform: rotate(-1.2deg);
  }
  75% {
    transform: rotate(0deg);
  }
}

.head-group {
  animation: head-bob 1.4s ease-in-out infinite;
  transform-origin: 104px 130px;
}
@keyframes head-bob {
  0%,
  100% {
    transform: translateY(0);
  }
  20% {
    transform: translateY(-3px);
  }
  50% {
    transform: translateY(0);
  }
  70% {
    transform: translateY(-3px);
  }
}

.backpack {
  animation: pack-bob 1.4s ease-in-out infinite;
  transform-origin: 89px 118px;
}
@keyframes pack-bob {
  0%,
  100% {
    transform: translateY(0);
  }
  15% {
    transform: translateY(-5px) rotate(1deg);
  }
  50% {
    transform: translateY(0);
  }
  65% {
    transform: translateY(-5px) rotate(-1deg);
  }
}

/* 四肢：交叉爬升步态 */
.back-leg {
  animation: climb-back-leg 1.4s ease-in-out infinite;
  transform-origin: 104px 160px;
}
.front-leg {
  animation: climb-front-leg 1.4s ease-in-out infinite;
  transform-origin: 104px 160px;
}
.back-arm {
  animation: climb-back-arm 1.4s ease-in-out infinite;
  transform-origin: 104px 108px;
}
.front-arm {
  animation: climb-front-arm 1.4s ease-in-out infinite;
  transform-origin: 104px 108px;
}

@keyframes climb-back-leg {
  0%,
  100% {
    transform: rotate(-30deg);
  }
  18% {
    transform: rotate(-14deg);
  }
  35% {
    transform: rotate(6deg);
  }
  50% {
    transform: rotate(30deg);
  }
  68% {
    transform: rotate(14deg);
  }
  85% {
    transform: rotate(-6deg);
  }
}
@keyframes climb-front-leg {
  0%,
  100% {
    transform: rotate(30deg);
  }
  18% {
    transform: rotate(14deg);
  }
  35% {
    transform: rotate(-6deg);
  }
  50% {
    transform: rotate(-30deg);
  }
  68% {
    transform: rotate(-14deg);
  }
  85% {
    transform: rotate(6deg);
  }
}
@keyframes climb-back-arm {
  0%,
  100% {
    transform: rotate(24deg);
  }
  18% {
    transform: rotate(10deg);
  }
  35% {
    transform: rotate(-8deg);
  }
  50% {
    transform: rotate(-22deg);
  }
  68% {
    transform: rotate(-10deg);
  }
  85% {
    transform: rotate(8deg);
  }
}
@keyframes climb-front-arm {
  0%,
  100% {
    transform: rotate(-22deg);
  }
  18% {
    transform: rotate(-10deg);
  }
  35% {
    transform: rotate(8deg);
  }
  50% {
    transform: rotate(24deg);
  }
  68% {
    transform: rotate(10deg);
  }
  85% {
    transform: rotate(-8deg);
  }
}

.pole-group {
  animation: pole-plant 1.4s ease-in-out infinite;
  transform-origin: 136px 138px;
}
@keyframes pole-plant {
  0%,
  100% {
    transform: rotate(-4deg);
  }
  25% {
    transform: rotate(8deg);
  }
  50% {
    transform: rotate(-4deg);
  }
  75% {
    transform: rotate(8deg);
  }
}

/* ======== 暗色模式 ======== */
.dark .sun-core {
  fill: #a07830;
}
.dark .sun-rays {
  stroke: #a07830;
}

.dark .birds {
  stroke: #808080;
}

.dark .mountains-far {
  fill: #1c1c1c;
}
.dark .mountains {
  fill: #252525;
}

.dark .trees {
  fill: #2e2e2e;
}

.dark .clouds {
  fill: #2c2c2c;
}

.dark .slope {
  stroke: #333;
}
.dark .trail {
  stroke: #2e2e2e;
}

.dark .shadow {
  fill: rgba(0, 0, 0, 0.3);
}
.dark .body,
.dark .backpack,
.dark .head,
.dark .hat {
  fill: #e5e5e5;
}
.dark .backpack rect:last-child {
  fill: rgba(229, 229, 229, 0.15);
}
.dark .limb path,
.dark .pole {
  stroke: #e5e5e5;
}

@media (max-width: 600px) {
  .hiker-svg {
    width: 320px;
    height: 384px;
  }
}
</style>
