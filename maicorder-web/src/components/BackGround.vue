<template>
  <div class="m3-bg-wrapper" ref="container">
    <canvas ref="canvasEl"></canvas>
    
    <div class="m3-bg-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const props = defineProps({
  // M3 Primary 颜色 (紫色系)
  lineColor: { type: String, default: 'rgba(103, 80, 164, 0.2)' },
  // M3 Surface 颜色 (极浅紫)
  bgColor: { type: String, default: '#F3EDF7' },
  // 波动速度
  speed: { type: Number, default: 0.004 },
  // 线条间距
  spacing: { type: Number, default: 16 }
});

const container = ref(null);
const canvasEl = ref(null);
let ctx = null;
let animationFrame = null;
let time = 0;
let ringCount = 0; // 动态计算出的圈数

/**
 * 核心逻辑：计算覆盖全屏所需的圈数
 * 原理：计算屏幕中心到最远角落的距离 (勾股定理)
 */
const calculateRings = (width, height) => {
  const centerX = width / 2;
  const centerY = height / 2;
  // 计算中心到角落的最大距离
  const maxRadius = Math.sqrt(Math.pow(centerX, 2) + Math.pow(centerY, 2));
  // 加上一小段缓冲，确保边缘完全覆盖
  return Math.ceil(maxRadius / props.spacing) + 5;
};

const updateSize = () => {
  if (!container.value || !canvasEl.value) return;
  const dpr = window.devicePixelRatio || 1;
  const rect = container.value.getBoundingClientRect();
  
  canvasEl.value.width = rect.width * dpr;
  canvasEl.value.height = rect.height * dpr;
  ctx.scale(dpr, dpr);
  
  canvasEl.value.style.width = `${rect.width}px`;
  canvasEl.value.style.height = `${rect.height}px`;

  // 重新计算圈数
  ringCount = calculateRings(rect.width, rect.height);
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
  ctx.lineWidth = 1.0;

  for (let i = 0; i < ringCount; i++) {
    ctx.beginPath();
    const baseRadius = i * props.spacing;
    const points = 100; // 采样点

    for (let j = 0; j <= points; j++) {
      const angle = (j / points) * Math.PI * 2;
      
      // 有机噪音算法：让线条产生不规则蠕动感
      const noise = 
        Math.sin(angle * 3 + time + i * 0.2) * (5 + i * 0.1) +
        Math.cos(angle * 2 - time * 0.5 + i * 0.1) * 4;

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
  position: fixed; /* 固定在后台 */
  inset: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
  background-color: #F3EDF7;
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
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>