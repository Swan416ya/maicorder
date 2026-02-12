<template>
  <div class="moving-rectangles-bg">
    <div class="bg-layer"></div>
    <canvas ref="canvasEl" class="rectangles-canvas"></canvas>
    
    <div class="bg-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const props = defineProps({
  // 背景颜色
  bgColor: { type: String, default: '#f5f5f5' },
  // 矩形颜色
  rectColor: { type: String, default: 'rgb(255, 255, 255)' },
  // 矩形数量
  rectCount: { type: Number, default: 6 },
  // 最小速度
  minSpeed: { type: Number, default: 0.2 },
  // 最大速度
  maxSpeed: { type: Number, default: 0.8 },
  // 最小透明度
  minOpacity: { type: Number, default: 0.6 },
  // 最大透明度
  maxOpacity: { type: Number, default: 0.8 }
});

const canvasEl = ref(null);
let ctx = null;
let animationFrame = null;
let canvasWidth = 0;
let canvasHeight = 0;
let dpr = 1;

// 矩形数组
let rectangles = [];

// 随机数生成函数
const random = (min, max) => Math.random() * (max - min) + min;
const randomInt = (min, max) => Math.floor(random(min, max));

// 创建矩形对象
const createRectangle = (index) => {
  // 使用canvasHeight的1/3到2/3作为尺寸范围
  const minSize = canvasHeight / 3;
  const maxSize = canvasHeight * 2 / 3;
  
  const width = randomInt(minSize, maxSize);
  const height = randomInt(minSize, maxSize);
  const speed = random(props.minSpeed, props.maxSpeed);
  const opacity = random(props.minOpacity, props.maxOpacity);
  
  // 矩形出现位置：中间80%的区域（去掉最上和最下的10%）
  const topMargin = canvasHeight * 0.1; // 上边10%不出现
  const bottomMargin = canvasHeight * 0.1; // 下边10%不出现
  const availableHeight = canvasHeight - topMargin - bottomMargin;
  
  // 确保矩形不超出中间区域
  const maxY = canvasHeight - bottomMargin - height;
  const y = randomInt(topMargin, Math.max(topMargin, maxY));
  
  // 从右侧外开始
  const x = randomInt(canvasWidth, canvasWidth + 800);
  
  return {
    id: index,
    x,
    y,
    width,
    height,
    speed,
    opacity,
    // 注意：这里只存储颜色和不透明度，不包含透明度值
    color: props.rectColor,
    alpha: opacity
  };
};

// 初始化所有矩形
const initRectangles = () => {
  rectangles = [];
  for (let i = 0; i < props.rectCount; i++) {
    rectangles.push(createRectangle(i));
  }
};

// 更新矩形位置
const updateRectangles = () => {
  rectangles.forEach(rect => {
    // 仅水平移动
    rect.x -= rect.speed;
    
    // 如果矩形完全移出左边，重置到右侧
    if (rect.x + rect.width < -50) {
      const minSize = canvasHeight / 3;
      const maxSize = canvasHeight * 2 / 3;
      
      rect.width = randomInt(minSize, maxSize);
      rect.height = randomInt(minSize, maxSize);
      rect.x = randomInt(canvasWidth, canvasWidth + 800);
      rect.speed = random(props.minSpeed, props.maxSpeed);
      rect.alpha = random(props.minOpacity, props.maxOpacity);
      
      // 重新计算中间区域的y坐标
      const topMargin = canvasHeight * 0.1;
      const bottomMargin = canvasHeight * 0.1;
      const maxY = canvasHeight - bottomMargin - rect.height;
      rect.y = randomInt(topMargin, Math.max(topMargin, maxY));
    }
  });
};

// 绘制矩形
const drawRectangles = () => {
  // 先绘制背景
  ctx.clearRect(0, 0, canvasWidth * dpr, canvasHeight * dpr);
  ctx.save();
  ctx.scale(dpr, dpr);
  ctx.fillStyle = props.bgColor;
  ctx.fillRect(0, 0, canvasWidth, canvasHeight);
  ctx.restore();
  
  // 绘制所有矩形
  rectangles.forEach(rect => {
    ctx.save();
    ctx.scale(dpr, dpr);
    
    // 设置全局透明度
    ctx.globalAlpha = rect.alpha;
    
    // 绘制直角矩形
    ctx.fillStyle = rect.color;
    ctx.fillRect(rect.x, rect.y, rect.width, rect.height);
    
    // 重置全局透明度
    ctx.globalAlpha = 1;
    ctx.restore();
  });
};

// 动画循环
const animate = () => {
  updateRectangles();
  drawRectangles();
  animationFrame = requestAnimationFrame(animate);
};

// 更新画布大小
const updateSize = () => {
  if (!canvasEl.value) return;
  
  const container = canvasEl.value.parentElement;
  if (!container) return;
  
  dpr = window.devicePixelRatio || 1;
  const rect = container.getBoundingClientRect();
  
  canvasWidth = rect.width;
  canvasHeight = rect.height;
  
  canvasEl.value.width = canvasWidth * dpr;
  canvasEl.value.height = canvasHeight * dpr;
  canvasEl.value.style.width = `${canvasWidth}px`;
  canvasEl.value.style.height = `${canvasHeight}px`;
  
  // 重新初始化矩形以适应新大小
  initRectangles();
};

onMounted(() => {
  if (!canvasEl.value) return;
  
  ctx = canvasEl.value.getContext('2d');
  updateSize();
  window.addEventListener('resize', updateSize);
  animate();
});

onUnmounted(() => {
  window.removeEventListener('resize', updateSize);
  if (animationFrame) {
    cancelAnimationFrame(animationFrame);
  }
});
</script>

<style scoped>
.moving-rectangles-bg {
  position: fixed;
  inset: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  margin: 0;
  padding: 0;
  z-index: 0;
}

.bg-layer {
  position: absolute;
  inset: 0;
  background-color: v-bind('props.bgColor');
  z-index: 0;
}

.rectangles-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none; /* 允许点击穿透 */
}

.bg-content {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>