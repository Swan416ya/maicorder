<template>
  <div class="vanta-like-container" ref="container">
    <canvas ref="canvas"></canvas>
    <!-- 这里可以放置你的 UI 内容 -->
    <div class="content">
      <slot>
        <h1 class="m3-title">Material 3 Waves</h1>
        <p class="m3-subtitle">Dynamic Fluid Background</p>
      </slot>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const canvas = ref(null);
const container = ref(null);
let ctx = null;
let animationFrame = null;
let time = 0;

// M3 配色方案
const colors = {
  background: '#F3EDF7', // Surface Container
  line: 'rgba(103, 80, 164, 0.3)', // Primary with opacity
};

const initCanvas = () => {
  ctx = canvas.value.getContext('2d');
  resize();
};

const resize = () => {
  if (container.value) {
    canvas.value.width = container.value.clientWidth;
    canvas.value.height = container.value.clientHeight;
  }
};

const draw = () => {
  if (!ctx) return;

  const { width, height } = canvas.value;
  ctx.clearRect(0, 0, width, height);
  
  // 背景填充
  ctx.fillStyle = colors.background;
  ctx.fillRect(0, 0, width, height);

  const centerX = width / 2;
  const centerY = height / 2;
  const ringCount = 40; // 线条数量
  const step = 15;      // 线条间距
  time += 0.005;        // 波动速度

  ctx.strokeStyle = colors.line;
  ctx.lineWidth = 1.5;

  for (let i = 0; i < ringCount; i++) {
    ctx.beginPath();
    
    const baseRadius = i * step + 50;
    const points = 120; // 每一圈的点数，越多越平滑

    for (let j = 0; j <= points; j++) {
      const angle = (j / points) * Math.PI * 2;
      
      // 核心算法：通过多层正弦波叠加模拟有机噪音效果
      // 这里的参数控制了波动的“起伏感”
      const noise = 
        Math.sin(angle * 5 + time + i * 0.2) * 8 +
        Math.cos(angle * 3 - time * 0.5 + i * 0.5) * 5 +
        Math.sin(i * 0.1 + time) * 3;

      const r = baseRadius + noise;
      const x = centerX + Math.cos(angle) * r;
      const y = centerY + Math.sin(angle) * r;

      if (j === 0) {
        ctx.moveTo(x, y);
      } else {
        ctx.lineTo(x, y);
      }
    }
    ctx.stroke();
  }

  animationFrame = requestAnimationFrame(draw);
};

onMounted(() => {
  initCanvas();
  window.addEventListener('resize', resize);
  draw();
});

onUnmounted(() => {
  window.removeEventListener('resize', resize);
  cancelAnimationFrame(animationFrame);
});
</script>

<style scoped>
.vanta-like-container {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background-color: #F3EDF7;
}

canvas {
  position: absolute;
  top: 0;
  left: 0;
  display: block;
}

.content {
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  pointer-events: none; /* 让鼠标事件穿透到背景（如果需要交互） */
}

.m3-title {
  font-family: 'Roboto', sans-serif;
  font-size: 4rem;
  font-weight: 700;
  color: #1D1B20; /* M3 On Surface */
  margin: 0;
}

.m3-subtitle {
  font-family: 'Roboto', sans-serif;
  font-size: 1.5rem;
  color: #49454F; /* M3 On Surface Variant */
  margin-top: 1rem;
}
</style>