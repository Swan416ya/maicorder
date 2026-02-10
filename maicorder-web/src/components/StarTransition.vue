<!-- StarTransition.vue -->
<template>
  <div v-if="!isDestroyed" class="transition-overlay">
    <!-- 背景扩张圆：初始透明度0，扩张时变为全屏 -->
    <div 
      :class="['expand-circle', { 'is-expanding': isExpanding }]" 
      :style="{ backgroundColor: bgColor }"
    />

    <!-- 中心星形加载器 -->
    <div :class="['loader-content', { 'fade-out': isExpanding }]">
      <div class="md3-star">
        <svg viewBox="0 0 100 100">
          <path d="M50 0 L61 39 L100 50 L61 61 L50 100 L39 61 L0 50 L39 39 Z" fill="#4F378B" />
        </svg>
      </div>
      <p>qwq加载中......</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const props = defineProps({
  bgColor: { type: String, default: '#F3EDF7' } // 与底层的 BackGround 背景色保持一致
});

const emit = defineEmits(['finished']);
const isExpanding = ref(false);
const isDestroyed = ref(false);

onMounted(() => {
  // 1. 加载 1 秒
  setTimeout(() => {
    isExpanding.value = true;

    // 2. 动画时间 0.8s 结束后，通知父组件销毁自己
    setTimeout(() => {
      isDestroyed.value = true;
      emit('finished');
    }, 800); 
  }, 1000);
});
</script>

<style scoped>
.transition-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #EADDFF; /* 初始加载时的背景色 */
  overflow: hidden;
}

.expand-circle {
  position: absolute;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  transform: scale(0);
  background-color: #F3EDF7; /* 这里设为底层 BackGround 的背景色 */
  transition: transform 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.expand-circle.is-expanding {
  transform: scale(500); /* 覆盖整个屏幕 */
}

.loader-content {
  position: relative;
  z-index: 2;
  transition: opacity 0.4s ease;
}

.loader-content.fade-out {
  opacity: 0;
}

.md3-star {
  width: 80px;
  height: 80px;
  animation: rotate 4s infinite linear;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>