<template>
  <div class="screen">
    <div class="header">
      <h1 class="title">Give Us a <span class="indent">Happy Ending</span></h1>
      <p class="feat">join MAICORDER</p>
    </div>

    <div class="card-wrapper" @click = "handleCardClick">
      <div class="card">
        <div class="corner top-left">
          <!-- <div class="num">2</div> -->
          <div class="suit">♥</div>
        </div>

        <div class="center-image-container">
          <img 
            v-bind:src="cardImage"
            alt="Custom Card Art" 
            class="card-image"
          />
        </div>

        <div class="corner bottom-right">
          <!-- <div class="num">2</div> -->
          <div class="suit">♥</div>
        </div>
      </div>
    </div>

    <div class="footer">
      <p>github</p>
      <p>Give Me a Happy Ending <span class="small-feat">MAICORDER</span></p>
    </div>
  </div>
</template>

<script setup>

import {ref} from 'vue'
import cardImage from '@/assets/rev-maicorder.png'
const handleCardClick = () => {
  window.open('https://github.com/Swan416ya/maicorder', '_blank')
  console.log('跳转github')
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@1,500;1,700&display=swap');

/* 全局屏幕样式 */
.screen {
  background-color: #121212;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 4rem 1rem;
  color: #eb3d4b;
  font-family: 'Playfair Display', serif;
  overflow: hidden;
  position: relative;
}

/* 标题和底部文字样式 */
.header { text-align: left; width: 100%; max-width: 600px; z-index: 10; }
.title { font-size: 4.5rem; line-height: 0.85; margin: 0; font-style: italic; }
.indent { display: block; margin-left: 2rem; }
.feat { font-family: sans-serif; font-size: 0.8rem; letter-spacing: 2px; margin-top: 0.5rem; opacity: 0.9; }

.footer { text-align: center; font-size: 0.75rem; letter-spacing: 1px; z-index: 10; }
.small-feat { opacity: 0.8; margin-left: 5px; }

/* 3D 场景设定 */
.card-wrapper {
  perspective: 1200px;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: -30px; /* 向上微调 */
}

/* 扑克牌主体 */
.card {
  width: 320px;
  height: 200px;
  background: #181818;
  border: 1px solid rgba(235, 61, 75, 0.4);
  border-radius: 12px;
  position: relative;
  /* 初始透视角度 */
  transform: rotateX(55deg) rotateZ(-35deg);
  /* 动画：浮动效果 */
  animation: float 4s ease-in-out infinite;
  box-shadow: 20px 40px 60px rgba(0,0,0,0.6);
  overflow: hidden; /* 确保图片不溢出卡面圆角 */
}

/* 角标绝对定位 (修复后的位置) */
.corner {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-weight: bold;
  z-index: 2; /* 确保在图片之上 */
}
.top-left { top: 15px; left: 15px; }
.bottom-right { bottom: 15px; right: 15px; transform: rotate(180deg); }

.num { font-size: 1.8rem; line-height: 1; color: #eb3d4b; }
.suit { font-size: 1.5rem; color: #eb3d4b; }

/* 关键修改：图片容器布局 */
.center-image-container {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 75%; /* 占用卡片宽度的 75% */
  height: 80%; /* 占用卡片高度的 80% */
  background-color: #1a1a1a; /* 图片未加载时的占位色 */
  border-radius: 8px; /* 图片区域圆角 */
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 图片样式 */
.card-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: cover; /* 确保图片填满容器并保持比例 */
  filter: sepia(30%) contrast(110%) brightness(95%); /* 添加一点复古和深色效果，使其融入 */
}

/* 浮动动画 */
@keyframes float {
  0%, 100% {
    transform: rotateX(55deg) rotateZ(-35deg) translateZ(0px);
    box-shadow: 20px 40px 60px rgba(0,0,0,0.6);
  }
  50% {
    transform: rotateX(52deg) rotateZ(-32deg) translateZ(30px);
    box-shadow: 40px 80px 100px rgba(0,0,0,0.4);
  }
}

/* 移动端适配 */
@media (max-width: 600px) {
  .title { font-size: 3rem; }
  .card { width: 260px; height: 160px; }
  .center-image-container { width: 70%; height: 75%; }
}
</style>