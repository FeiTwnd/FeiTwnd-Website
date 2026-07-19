<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  dark: { type: Boolean, default: false },
  ready: { type: Boolean, default: false }
})

const phase = ref('playing')
const timers = []
const mountedAt = Date.now()
const MIN_DISPLAY = 2500

function startFade() {
  if (phase.value !== 'playing') return
  const elapsed = Date.now() - mountedAt
  const delay = Math.max(0, MIN_DISPLAY - elapsed)
  timers.push(
    setTimeout(() => {
      phase.value = 'fading'
    }, delay)
  )
  timers.push(
    setTimeout(() => {
      phase.value = 'done'
    }, delay + 1500)
  )
}

watch(
  () => props.ready,
  (v) => {
    if (v) startFade()
  }
)

onMounted(() => {
  timers.push(
    setTimeout(() => {
      if (phase.value === 'playing') startFade()
    }, 6000)
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

      <!-- 登山小人（外层沿小径漂移） -->
      <g class="hiker-wrap">
        <g class="hiker">
          <!-- 投影 -->
          <ellipse class="shadow" cx="102" cy="196" rx="26" ry="5" />

          <!-- 后腿（大腿 + 小腿/靴，膝关节分段） -->
          <g class="leg back-leg">
            <path class="thigh" d="M100 148 L100 172" />
            <g class="shin-group">
              <path
                class="shin"
                d="M100 172 L100 191 Q100 194 103 194 L111 194"
              />
            </g>
          </g>

          <!-- 后臂（上臂 + 前臂，肘关节分段） -->
          <g class="arm back-arm">
            <path class="upper-arm" d="M100 103 L100 125" />
            <g class="forearm-group">
              <path class="forearm" d="M100 125 L98 141" />
            </g>
          </g>

          <!-- 登山包 -->
          <g class="backpack">
            <rect
              class="pack-roll"
              x="77"
              y="95"
              width="26"
              height="9"
              rx="4.5"
            />
            <rect
              class="pack-main"
              x="76"
              y="102"
              width="27"
              height="45"
              rx="9"
            />
            <rect
              class="pack-pocket"
              x="81"
              y="126"
              width="15"
              height="11"
              rx="4"
            />
            <path class="pack-strap" d="M99 105 Q89 113 87 128" />
          </g>

          <!-- 躯干 -->
          <path
            class="torso"
            d="M95 100 Q100 96 105 100 L109 150 Q100 155 91 150 Z"
          />

          <!-- 头部与遮阳帽 -->
          <g class="head-group">
            <circle class="head" cx="104" cy="87" r="12" />
            <path
              class="hat"
              d="M90 82 Q90 69 104 68 Q118 69 118 80 L127 82 Q129 85 123 85 L94 86 Q88 86 90 82 Z"
            />
          </g>

          <!-- 前腿 -->
          <g class="leg front-leg">
            <path class="thigh" d="M100 148 L100 172" />
            <g class="shin-group">
              <path
                class="shin"
                d="M100 172 L100 191 Q100 194 103 194 L111 194"
              />
            </g>
          </g>

          <!-- 前臂（握持登山杖随臂摆动） -->
          <g class="arm front-arm">
            <path class="upper-arm" d="M100 103 L100 125" />
            <g class="forearm-group">
              <path class="forearm" d="M100 125 L104 141" />
              <line class="pole" x1="103" y1="132" x2="120" y2="190" />
            </g>
          </g>
        </g>
      </g>
    </svg>
    <p class="splash-copy">正在打包行囊…</p>
  </div>
</template>

<style scoped>
.footprint-splash {
  position: fixed;
  inset: 0;
  z-index: 10000;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
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

.splash-copy {
  font-family: 'Noto Serif SC', Georgia, 'Times New Roman', serif;
  font-size: 13px;
  color: #909399;
  letter-spacing: 0.08em;
  user-select: none;
  animation: copy-breathe 2s ease-in-out infinite;
}
@keyframes copy-breathe {
  0%,
  100% {
    opacity: 0.3;
  }
  50% {
    opacity: 0.8;
  }
}
.dark .splash-copy {
  color: #808080;
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
.torso,
.head,
.hat,
.pack-roll,
.pack-main {
  fill: #303133;
}
.pack-pocket {
  fill: rgba(48, 49, 51, 0.18);
}
.thigh,
.shin,
.upper-arm,
.forearm,
.pack-strap,
.pole {
  stroke: #303133;
  fill: none;
  stroke-linecap: round;
  stroke-linejoin: round;
}
.thigh {
  stroke-width: 10;
}
.shin {
  stroke-width: 8.5;
}
.upper-arm {
  stroke-width: 8;
}
.forearm {
  stroke-width: 7;
}
.pack-strap {
  stroke-width: 3;
}
.pole {
  stroke-width: 3;
}
.shadow {
  fill: rgba(48, 49, 51, 0.12);
}

/* ============ 登山小人动画（步态周期 1.2s，一次循环两步） ============ */

.hiker-wrap {
  animation: drift-forward 3s ease-out forwards;
}
@keyframes drift-forward {
  from {
    translate: 0 0;
  }
  to {
    translate: 16px -5px;
  }
}

.hiker {
  rotate: 5deg;
  transform-origin: 100px 190px;
  animation: hiker-bob 1.2s ease-in-out infinite;
}
@keyframes hiker-bob {
  0%,
  50%,
  100% {
    translate: 0 0;
  }
  25%,
  75% {
    translate: 0 -3.5px;
  }
}

.shadow {
  animation: shadow-pulse 1.2s ease-in-out infinite;
  transform-origin: 102px 196px;
}
@keyframes shadow-pulse {
  0%,
  50%,
  100% {
    transform: scale(1);
    opacity: 1;
  }
  25%,
  75% {
    transform: scale(0.84);
    opacity: 0.6;
  }
}

.torso {
  animation: torso-sway 1.2s ease-in-out infinite;
  transform-origin: 100px 149px;
}
@keyframes torso-sway {
  0%,
  100% {
    transform: rotate(1.6deg);
  }
  25% {
    transform: rotate(0deg);
  }
  50% {
    transform: rotate(-1.6deg);
  }
  75% {
    transform: rotate(0deg);
  }
}

.head-group {
  animation: head-bob 1.2s ease-in-out infinite;
  transform-origin: 104px 99px;
}
@keyframes head-bob {
  0%,
  100% {
    transform: translateY(0) rotate(-1.2deg);
  }
  25% {
    transform: translateY(-1.5px) rotate(0deg);
  }
  50% {
    transform: translateY(0) rotate(1.2deg);
  }
  75% {
    transform: translateY(-1.5px) rotate(0deg);
  }
}

.backpack {
  animation: pack-lag 1.2s ease-in-out infinite;
  transform-origin: 89px 125px;
}
@keyframes pack-lag {
  0%,
  50%,
  100% {
    transform: translateY(0) rotate(0deg);
  }
  30%,
  80% {
    transform: translateY(-2px) rotate(-1.4deg);
  }
}

/* 四肢共用步态关键帧，对侧肢体用 -0.6s 负延迟严格反相 */
.front-leg {
  animation: gait-thigh 1.2s ease-in-out infinite;
  transform-origin: 100px 148px;
}
.back-leg {
  animation: gait-thigh 1.2s ease-in-out -0.6s infinite;
  transform-origin: 100px 148px;
}
.front-leg .shin-group {
  animation: gait-shin 1.2s ease-in-out infinite;
  transform-origin: 100px 172px;
}
.back-leg .shin-group {
  animation: gait-shin 1.2s ease-in-out -0.6s infinite;
  transform-origin: 100px 172px;
}
.front-arm {
  animation: gait-uarm 1.2s ease-in-out infinite;
  transform-origin: 100px 103px;
}
.back-arm {
  animation: gait-uarm 1.2s ease-in-out -0.6s infinite;
  transform-origin: 100px 103px;
}
.front-arm .forearm-group {
  animation: gait-farm 1.2s ease-in-out infinite;
  transform-origin: 100px 125px;
}
.back-arm .forearm-group {
  animation: gait-farm 1.2s ease-in-out -0.6s infinite;
  transform-origin: 100px 125px;
}

/* 大腿绕髋部摆动（负角度 = 向前迈）：0% 脚跟着地 → 50% 脚尖离地 → 100% 再次着地 */
@keyframes gait-thigh {
  0%,
  100% {
    transform: rotate(-20deg);
  }
  25% {
    transform: rotate(0deg);
  }
  50% {
    transform: rotate(20deg);
  }
  75% {
    transform: rotate(0deg);
  }
}

/* 膝关节屈膝（正角度 = 向后勾，符合人体关节方向）：支撑期接近伸直，摆动中期屈膝抬踵 */
@keyframes gait-shin {
  0%,
  100% {
    transform: rotate(2deg);
  }
  25% {
    transform: rotate(8deg);
  }
  50% {
    transform: rotate(12deg);
  }
  62% {
    transform: rotate(32deg);
  }
  72% {
    transform: rotate(40deg);
  }
  85% {
    transform: rotate(15deg);
  }
}

/* 手臂绕肩摆动，与同侧腿反相 */
@keyframes gait-uarm {
  0%,
  100% {
    transform: rotate(15deg);
  }
  25% {
    transform: rotate(0deg);
  }
  50% {
    transform: rotate(-15deg);
  }
  75% {
    transform: rotate(0deg);
  }
}

/* 肘部弯曲（负角度 = 向前弯，符合关节方向）：前摆时弯曲加深 */
@keyframes gait-farm {
  0%,
  100% {
    transform: rotate(-8deg);
  }
  50% {
    transform: rotate(-20deg);
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
.dark .torso,
.dark .head,
.dark .hat,
.dark .pack-roll,
.dark .pack-main {
  fill: #e5e5e5;
}
.dark .pack-pocket {
  fill: rgba(229, 229, 229, 0.15);
}
.dark .thigh,
.dark .shin,
.dark .upper-arm,
.dark .forearm,
.dark .pack-strap,
.dark .pole {
  stroke: #e5e5e5;
}

@media (max-width: 600px) {
  .hiker-svg {
    width: 320px;
    height: 384px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .hiker-svg * {
    animation-duration: 0s !important;
    animation-delay: 0s !important;
    animation-iteration-count: 1 !important;
  }
  .splash-copy {
    animation: none !important;
    opacity: 0.6;
  }
}
</style>
