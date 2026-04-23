<template>
  <BackGround>
    <div class="main-page-container">
      <!-- 顶部标题 -->
      <div class="page-title">
        <div class="title-line">{{ userName }}</div>
        <div class="title-line">今天勤了吗</div>
      </div>

      <!-- 核心区域：圆形按钮/弹窗容器 -->
      <div class="core-content">
        <!-- 主要按钮容器 -->
        <div class="check-btn-container" 
             :class="{ 'dialog-expanded': isDialogOpen }" 
             @click="handleCheckIn">
          <!-- 动态内接长方形容器 -->
          <div class="dialog-content-wrapper" v-if="isDialogOpen" :style="{
            width: contentWidth + 'px',
            height: contentHeight + 'px',
            borderRadius: '16px'
          }">
            <CheckInDialog @close="closeDialog" />
          </div>

          <span class="btn-text" v-if="!isDialogOpen">勤了</span>
        </div>
      </div>

      <!-- 关闭按钮，仅在弹窗打开时显示 -->
      <div v-if="isDialogOpen" class="close-button" @click="closeDialog">
        <svg viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
          <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
        </svg>
      </div>

      <!-- 底部功能按钮栏 -->
      <div class="bottom-buttons" v-if="!isDialogOpen">
        <button class="bottom-btn" @click="handleViewRecords">
          <div class="bottom-btn-icon">
            <svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
              <path d="M4 6h2v2H4V6zm0 5h2v2H4v-2zm0 5h2v2H4v-2zm18-10v2H8V6h14zm0 5v2H8v-2h14zm0 5v2H8v-2h14z"/>
            </svg>
          </div>
          <span class="bottom-btn-text">游玩记录</span>
        </button>
        
        <button class="bottom-btn" @click="moreTool">
          <div class="bottom-btn-icon">
            <svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-2-9h4v2h-4zm0 4h4v2h-4z"/>
            </svg>
          </div>
          <span class="bottom-btn-text">更多工具</span>
        </button>
        
        <button class="bottom-btn" @click="goToB50Page">
          <div class="bottom-btn-icon">
            <svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
              <path d="M12 5.69l5 4.5V18h-2v-6H9v6H7v-7.81l5-4.5M12 3L2 12h3v8h6v-6h2v6h6v-8h3L12 3z"/>
            </svg>
          </div>
          <span class="bottom-btn-text">B50</span>
        </button>
        
        <button class="bottom-btn" @click="handleLogout">
          <div class="bottom-btn-icon">
            <svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
              <path d="M10.09 15.59L11.5 17l5-5-5-5-1.41 1.41L12.67 11H3v2h9.67l-2.58 2.59zM19 3H5c-1.11 0-2 .9-2 2v4h2V5h14v14H5v-4H3v4c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z"/>
            </svg>
          </div>
          <span class="bottom-btn-text">退出登录</span>
        </button>
      </div>
    </div>
  </BackGround>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import CheckInDialog from '../components/CheckInDialog.vue'
import BackGround from '../components/BackGround.vue'

const router = useRouter()

// 基础变量
const userName = ref('未知用户')

// 动效相关变量
const isDialogOpen = ref(false)
const dialogSize = ref(200)
const maxDialogSize = ref(0)
// 动态内接长方形尺寸
const contentWidth = ref(0)
const contentHeight = ref(0)

// 页面挂载时初始化
onMounted(() => {
  initUserInfo()
  calculateMaxDialogSize()
  window.addEventListener('resize', handleWindowResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleWindowResize)
})

// 初始化用户名
const initUserInfo = () => {
  try {
    const userStr = localStorage.getItem('currentUser')
    if (userStr) {
      const userInfo = JSON.parse(userStr)
      userName.value = userInfo.username || userInfo.name || '未知用户'
    }
  } catch (error) {
    console.error('获取用户信息失败：', error)
  }
}

// 计算弹窗最大尺寸 + 动态内接长方形尺寸
const calculateMaxDialogSize = () => {
  const windowWidth = window.innerWidth
  const windowHeight = window.innerHeight
  const longSide = Math.max(windowWidth, windowHeight)

  // 设置圆的最大直径（屏幕长边的 75%）
  maxDialogSize.value = Math.floor(longSide * 0.75)

  // 如果弹窗是打开状态，保持尺寸同步
  if (isDialogOpen.value) {
    dialogSize.value = maxDialogSize.value
  }

  // 圆的直径（使用98%以确保边缘显示）
  const diameter = maxDialogSize.value * 0.98
  
  // 设置一个合理的内接矩形最小高度（屏幕高度的60%）
  const minHeightRatio = 0.6
  const targetMinHeight = windowHeight * minHeightRatio
  
  if (windowWidth > windowHeight) {
    // 横屏逻辑
    let height = windowHeight * 0.9
    if (height > diameter) height = diameter

    contentHeight.value = Math.floor(height)
    contentWidth.value = Math.floor(Math.sqrt(Math.pow(diameter, 2) - Math.pow(contentHeight.value, 2)))
  } else {
    // 竖屏逻辑（手机主要模式）
    let height = Math.min(windowHeight * 0.8, diameter * 0.8)
    
    // 确保高度不小于最小高度
    if (height < targetMinHeight && targetMinHeight <= diameter) {
      height = targetMinHeight
    }
    
    contentHeight.value = Math.floor(height)
    contentWidth.value = Math.floor(Math.sqrt(Math.pow(diameter, 2) - Math.pow(contentHeight.value, 2)))
    
    // 确保宽度不会太大（不超过屏幕宽度的95%）
    const maxWidth = windowWidth * 0.95
    if (contentWidth.value > maxWidth) {
      contentWidth.value = Math.floor(maxWidth)
      contentHeight.value = Math.floor(Math.sqrt(Math.pow(diameter, 2) - Math.pow(contentWidth.value, 2)))
    }
  }
}

// 窗口大小变化时重新计算
const handleWindowResize = () => {
  calculateMaxDialogSize()
}

// 打开弹窗
const openDialog = () => {
  isDialogOpen.value = true
  setTimeout(() => {
    dialogSize.value = maxDialogSize.value
  }, 10)
}

// 关闭弹窗
const closeDialog = () => {
  isDialogOpen.value = false
  dialogSize.value = 200
}

// 点击「勤了」按钮
const handleCheckIn = () => {
  if (!isDialogOpen.value) {
    openDialog()
  }
}

// 查看我的记录
const handleViewRecords = () => {
  router.push('/records')
}

// 登出逻辑
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('currentUser')
  alert('已退出登录')
  router.push('/')
}

// 更多工具
const moreTool = () => {
  router.push('/moretoolpage')
}

// 个人主页
const goToB50Page = () => {
  router.push('/tool/wmB50Page')
}
</script>

<style scoped>
.main-page-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

.page-title {
  position: absolute;
  top: 120px;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  color: #000;
  z-index: 1;
}

.title-line {
  font-size: 1.6rem;
  font-weight: 600;
  margin: 2px 0;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.core-content {
  margin-top: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20px;
  z-index: 2;
  position: relative;
  flex: 1;
}

/* 关闭按钮 */
.close-button {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 1000;
  cursor: pointer;
  color: #000;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: none;
  padding: 0;
}

.close-button:hover {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.1);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.close-button:active {
  transform: scale(0.95);
  transition: transform 0.1s ease;
}

/* 底部按钮容器 */
.bottom-buttons {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 20px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  border-radius: 50px;
  padding: 12px 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  z-index: 2;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

/* 底部按钮样式 */
.bottom-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 12px;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 80px;
  position: relative;
  color: #333;
}

.bottom-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.bottom-btn:active {
  transform: translateY(0);
  transition: transform 0.1s ease;
}

/* 按钮图标 */
.bottom-btn-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6px;
  color: #000;
  transition: all 0.3s ease;
}

.bottom-btn:hover .bottom-btn-icon {
  color: #000;
  transform: scale(1.1);
}

/* 按钮文字 */
.bottom-btn-text {
  font-size: 0.75rem;
  font-weight: 500;
  color: #333;
  text-align: center;
  line-height: 1.2;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.bottom-btn:hover .bottom-btn-text {
  color: #000;
  font-weight: 600;
}

/* 分隔线 */
.bottom-btn:not(:last-child)::after {
  content: '';
  position: absolute;
  right: -10px;
  top: 50%;
  transform: translateY(-50%);
  width: 1px;
  height: 24px;
  background: rgba(0, 0, 0, 0.1);
}

.check-btn-container {
  width: 240px;
  height: 240px;
  border-radius: 50%;
  background-color: #000;
  color: #fff;
  font-size: 2.2rem;
  font-weight: 600;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  overflow: hidden;
  position: relative;
  z-index: 3;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  border: 2px solid #000;
}

.check-btn-container:hover {
  transform: scale(1.02);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.3);
  background: #111;
}

.check-btn-container.dialog-expanded {
  background-color: #fff;
  cursor: default;
  width: v-bind(dialogSize + 'px');
  height: v-bind(dialogSize + 'px');
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.3);
  border: none;
}

/* 动态内接长方形容器 */
.dialog-content-wrapper {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  box-sizing: border-box;
  width: 100%;
  height: 100%;
  padding: 30px 10px;
  overflow-y: auto;
  overflow-x: hidden;
  -webkit-mask-image: linear-gradient(to bottom,
      rgba(0, 0, 0, 0) 0%,
      rgba(0, 0, 0, 0) 5%,
      rgba(0, 0, 0, 1) 20%,
      rgba(0, 0, 0, 1) 80%,
      rgba(0, 0, 0, 0) 95%,
      rgba(0, 0, 0, 0) 100%);
  mask-image: linear-gradient(to bottom,
      rgba(0, 0, 0, 0) 0%,
      rgba(0, 0, 0, 0) 5%,
      rgba(0, 0, 0, 1) 20%,
      rgba(0, 0, 0, 1) 80%,
      rgba(0, 0, 0, 0) 95%,
      rgba(0, 0, 0, 0) 100%);
  transition: all 0.5s ease-in-out;
}

/* 隐藏滚动条 */
.dialog-content-wrapper::-webkit-scrollbar {
  display: none;
}

.dialog-content-wrapper {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.btn-text {
  transition: all 0.5s ease;
  letter-spacing: 2px;
  text-transform: uppercase;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .page-title {
    top: 100px;
  }
  
  .title-line {
    font-size: 1.4rem;
  }
  
  .close-button {
    top: 15px;
    left: 15px;
    width: 36px;
    height: 36px;
  }
  
  .bottom-buttons {
    gap: 12px;
    padding: 10px 20px;
    bottom: 30px;
    max-width: 95vw;
  }
  
  .bottom-btn {
    min-width: 65px;
    padding: 8px 12px;
  }
  
  .bottom-btn-icon {
    width: 18px;
    height: 18px;
    margin-bottom: 4px;
  }
  
  .bottom-btn-text {
    font-size: 0.65rem;
  }
  
  .check-btn-container {
    width: 200px;
    height: 200px;
    font-size: 1.8rem;
  }
  
  .check-btn-container.dialog-expanded {
    width: v-bind(dialogSize + 'px');
    height: v-bind(dialogSize + 'px');
  }
  
  .dialog-content-wrapper {
    padding: 20px 10px;
  }
}

@media (max-width: 480px) {
  .page-title {
    top: 80px;
  }
  
  .title-line {
    font-size: 1.3rem;
    letter-spacing: 1px;
  }
  
  .close-button {
    top: 10px;
    left: 10px;
    width: 32px;
    height: 32px;
  }
  
  .bottom-buttons {
    gap: 8px;
    padding: 8px 16px;
    bottom: 20px;
    border-radius: 40px;
    max-width: 95vw;
  }
  
  .bottom-btn {
    min-width: 50px;
    padding: 6px 8px;
  }
  
  .bottom-btn-icon {
    width: 16px;
    height: 16px;
    margin-bottom: 2px;
  }
  
  .bottom-btn-text {
    font-size: 0.6rem;
  }
  
  .check-btn-container {
    width: 180px;
    height: 180px;
    font-size: 1.6rem;
  }
  
  .dialog-content-wrapper {
    padding: 15px 8px;
  }
  
  /* 在小屏幕上缩小按钮而不是折叠 */
  .bottom-buttons {
    flex-wrap: nowrap;
    justify-content: space-around;
  }
  
  .bottom-btn:not(:last-child)::after {
    display: none;
  }
}
</style>