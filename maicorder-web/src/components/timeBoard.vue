<template>
  <div class="time-board">
    <div class="time-display">{{ formattedTime }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const formattedTime = ref('')
let timer = null

const updateTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  
  formattedTime.value = `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
/* 引入像素字体 VT323 */
@import url('https://fonts.googleapis.com/css2?family=VT323&display=swap');

.time-board {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 10px 0;
}

.time-display {
  /* 使用像素字体，如果加载失败则回退到等宽字体 */
  font-family: 'VT323', 'Courier New', monospace;
  /* 放大字体 */
  font-size: 3rem; 
  /* 确保行高紧凑，避免上下留白过多 */
  line-height: 1;
  color: #000000;
  
  /* 去除边框和背景 */
  background-color: transparent;
  border: none;
  box-shadow: none;
  padding: 0;
  
  /* 可选：保持一点字间距让数字更清晰 */
  letter-spacing: 2px;
}
</style>