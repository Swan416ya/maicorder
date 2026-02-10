<template>
  <div class="m3-bg-wrapper" ref="container">
    <canvas ref="canvasEl"></canvas>
    <!-- 插槽：用于在背景上放置内容 -->
    <div class="m3-bg-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';

const props = defineProps({
  // M3 Primary 颜色
  lineColor: { type: String, default: 'rgba(103, 80, 164, 0.25)' },
  // M3 Surface 颜色
  bgColor: { type: String, default: '#F3EDF7' },
  // 波动速度
  speed: { type: Number, default: 0.005 },
  // 线条密度（环数）
  density: { type: Number, default: 50 },
  // 间距
  spacing: { type: Number, default: 14 }
});

const container = ref(null);
const canvasEl = ref(null);
let ctx = null;
let animationFrame = null;
let time = 0;

// 初始化 Canvas 尺寸，处理高分屏模糊问题
const updateSize = () => {
  if (!container.value || !canvasEl.value) return;
  const dpr = window.devicePixelRatio || 1;
  const rect = container.value.getBoundingClientRect();
  
  canvasEl.value.width = rect.width * dpr;
  canvasEl.value.height = rect.height * dpr;
  ctx.scale(dpr, dpr);
  
  canvasEl.value.style.width = `${rect.width}px`;
  canvasEl.value.style.height = `${rect.height}px`;
};

const draw = () => {
  if (!ctx || !canvasEl.value) return;

  const width = canvasEl.value.width / (window.devicePixelRatio || 1);
  const height = canvasEl.value.height / (window.devicePixelRatio || 1);

  ctx.clearRect(0, 0, width, height);
  ctx.fillStyle = props.bgColor;
  ctx.fillRect(0, 0, width, height);

  const centerX = width / 2;
  const centerY = height / 2;
  
  time += props.speed;

  ctx.strokeStyle = props.lineColor;
  ctx.lineWidth = 1.2;

  // 绘制有机波纹
  for (let i = 0; i < props.density; i++) {
    ctx.beginPath();
    const baseRadius = i * props.spacing + 40;
    const points = 120; // 点越多圆周越细腻

    for (let j = 0; j <= points; j++) {
      const angle = (j / points) * Math.PI * 2;
      
      // 多叠层噪音算法，模拟 Vanta 的有机感
      const noise = 
        Math.sin(angle * 4 + time + i * 0.3) * 7 +
        Math.cos(angle * 2 - time * 0.8 + i * 0.2) * 5 +
        Math.sin(i * 0.15 + time * 1.2) * 4;

      const r = baseRadius + noise;
      const x = centerX + Math.cos(angle) * r;
      const y = centerY + Math.sin(angle) * r;

      if (j === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    }
    ctx.stroke();
  }

  animationFrame = requestAnimationFrame(draw);
};

onMounted(() => {
  ctx = canvasEl.value.getContext('2d');
  updateSize();
  window.addEventListener('resize', updateSize);
  draw();
});

onUnmounted(() => {
  window.removeEventListener('resize', updateSize);
  cancelAnimationFrame(animationFrame);
});
</script>

<style scoped>
.m3-bg-wrapper {
  position: relative;
  width: 100%;
  height: 100%; /* 继承父级高度 */
  min-height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

canvas {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 0;
}

.m3-bg-content {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
}
</style>    