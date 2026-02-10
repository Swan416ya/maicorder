<template>
  <div class="md-container">
    <!-- 背景扩散层 (默认与背景同色，激活时变为深色并铺满) -->
    <div :class="['reveal-overlay', { 'is-active': isExpanding }]"></div>

    <!-- 中心加载区域 -->
    <div :class="['loader-content', { 'fade-out': isExpanding }]">
      <div class="shape-wrapper">
        <!-- 外部浅色圆圈 (对应图2) -->
        <div class="outer-ring"></div>
        
        <!-- 内部 MD3 圆角多边形 (对应图1) -->
        <svg class="md3-polygon" viewBox="0 0 100 100">
          <!-- 使用路径绘制圆角五边形 -->
          <path 
            d="M50 5 
               L85 30 
               L75 75 
               L25 75 
               L15 30 
               Z" 
            stroke-linejoin="round" 
            stroke-width="12" 
            stroke="#4F378B" 
            fill="#4F378B"
          />
        </svg>
      </div>
      <p class="label">正在加载</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const isExpanding = ref(false);

onMounted(() => {
  // 1. 等待 1 秒开始扩散
  setTimeout(() => {
    isExpanding.value = true;

    // 2. 动画结束后跳转页面 (这里用 console 模拟)
    setTimeout(() => {
      console.log("执行跳转...");
      // router.push('/home');
    }, 800); // 对应 CSS transition 时间
  }, 1000);
});
</script>

<style scoped>
/* 基础容器 */
.md-container {
  position: fixed;
  inset: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #F3EDF7; /* MD3 浅紫色背景 */
  overflow: hidden;
}

/* 核心：背景扩散动画 */
.reveal-overlay {
  position: absolute;
  width: 100vmax; /* 确保圆够大 */
  height: 100vmax;
  background-color: #4F378B; /* 扩散后的颜色 */
  border-radius: 50%;
  transform: scale(0);
  transition: transform 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1;
}

.reveal-overlay.is-active {
  transform: scale(2); /* 扩散铺满全屏 */
}

/* 加载内容层 */
.loader-content {
  position: relative;
  z-index: 2;
  text-align: center;
  transition: opacity 0.4s ease;
}

.loader-content.fade-out {
  opacity: 0;
}

/* 形状组合 */
.shape-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
}

/* 外圈圆环 */
.outer-ring {
  position: absolute;
  inset: -10px;
  border-radius: 50%;
  background-color: #D0BCFF; /* 图2中的淡紫色圆圈 */
  opacity: 0.6;
}

/* MD3 多边形 SVG */
.md3-polygon {
  width: 100%;
  height: 100%;
  /* 旋转动画 */
  animation: rotate-breathe 3s infinite ease-in-out;
}

.label {
  color: #4F378B;
  font-family: sans-serif;
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 形状旋转和微弱呼吸动画 */
@keyframes rotate-breathe {
  0% {
    transform: rotate(0deg) scale(1);
  }
  50% {
    transform: rotate(180deg) scale(1.1);
  }
  100% {
    transform: rotate(360deg) scale(1);
  }
}
</style>