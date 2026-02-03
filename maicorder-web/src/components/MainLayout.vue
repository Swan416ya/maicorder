<!-- MainLayout.vue -->
<template>
  <div class="main-page-container">
    <!-- 顶部标题 -->
    <div class="page-title">
      <div class="title-line">{{ userName }}</div>
      <div class="title-line">今天勤了</div>
    </div>

    <!-- 核心区域：圆形按钮/弹窗容器 -->
    <div class="core-content">
      <div 
        class="check-btn-container"
        :class="{ 'dialog-expanded': isDialogOpen }"
        @click="handleCheckIn"
        @click.stop="preventMaskClose"
      >
        <!-- 动态内接长方形容器（勾股定理计算尺寸 + 四周渐变羽化 + 上下留白） -->
        <div 
          class="dialog-content-wrapper"
          v-if="isDialogOpen"
          :style="{ 
            width: contentWidth + 'px', 
            height: contentHeight + 'px',
            borderRadius: '16px'
          }"
        >
          <CheckInDialog 
            @close="closeDialog"
          />
        </div>

        <span class="btn-text" v-if="!isDialogOpen">勤了</span>
      </div>

      <div class="view-records-link" @click="handleViewRecords" v-if="!isDialogOpen">
        我的记录
      </div>
    </div>

    <!-- 遮罩层 -->
    <div 
      class="dialog-mask"
      v-if="isDialogOpen"
      @click="closeDialog"
    >
      <p class="mask-tip">点击空白处返回</p>
    </div>

    <!-- 右下角登出按钮 -->
    <button class="logout-btn" @click="handleLogout">登出</button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import CheckInDialog from './CheckInDialog.vue'

const router = useRouter()

// 基础变量
const userName = ref('未知用户')

// 动效相关变量
const isDialogOpen = ref(false)
const dialogSize = ref(200)
const maxDialogSize = ref(0)
// 动态内接长方形尺寸（勾股定理计算）
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

// 计算弹窗最大尺寸 + 动态内接长方形尺寸（核心逻辑：改为90%基数）
const calculateMaxDialogSize = () => {
  const windowWidth = window.innerWidth
  const windowHeight = window.innerHeight
  const longSide = Math.max(windowWidth, windowHeight)
  maxDialogSize.value = Math.floor(longSide * 0.75)
  if (dialogSize.value > maxDialogSize.value) {
    dialogSize.value = maxDialogSize.value
  }

  // ########## 修改：基数改为圆直径的90% ##########
  const diameter = dialogSize.value * 0.9 // 对角线 = 圆直径 × 90%
  const screenRatioBase = 0.9 // 宽/高也取90%，符合需求

  if (windowWidth > windowHeight) {
    // 横屏：高 = 屏幕高度 × 90%，宽 = √(对角线² - 高²)
    contentHeight.value = Math.floor(windowHeight * screenRatioBase)
    contentWidth.value = Math.floor(Math.sqrt(Math.pow(diameter, 2) - Math.pow(contentHeight.value, 2)))
  } else {
    // 竖屏：宽 = 屏幕宽度 × 90%，高 = √(对角线² - 宽²)
    contentWidth.value = Math.floor(windowWidth * screenRatioBase)
    contentHeight.value = Math.floor(Math.sqrt(Math.pow(diameter, 2) - Math.pow(contentWidth.value, 2)))
  }

  // ########## 新增：上下留白处理（额外减少高度，预留留白空间） ##########
  contentHeight.value = Math.floor(contentHeight.value * 0.9)
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

// 防止事件冒泡
const preventMaskClose = () => {}

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
</script>

<style scoped>
.main-page-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #fafafa;
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
}

.core-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  z-index: 2;
}

/* 圆形按钮/弹窗容器：去掉黑色边框 */
.check-btn-container {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background-color: #000;
  /* 修改：删除黑色边框属性 */
  color: #fff;
  font-size: 2rem;
  font-weight: 600;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.5s ease-in-out;
  cursor: pointer;
  overflow: hidden;
  position: relative;
}

/* 弹窗展开状态：去掉黑色边框 */
.check-btn-container.dialog-expanded {
  background-color: #fff;
  /* 修改：删除边框宽度属性 */
  cursor: default;
  width: v-bind(dialogSize + 'px');
  height: v-bind(dialogSize + 'px');
}

/* 动态内接长方形容器：四周渐变羽化 + 上下留白 */
.dialog-content-wrapper {
  /* 布局：居中对齐内部内容，上下留白 */
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 30px 16px; /* 修改：上下30px留白，左右16px内边距 */
  box-sizing: border-box;
  /* 修改：四周渐变消失的羽化效果（mask实现） */
  -webkit-mask-image: linear-gradient(
    to top, transparent 0%, rgba(0, 0, 0, 1) 15%,
    rgba(0, 0, 0, 1) 85%, transparent 100%
  ), linear-gradient(
    to left, transparent 0%, rgba(0, 0, 0, 1) 15%,
    rgba(0, 0, 0, 1) 85%, transparent 100%
  );
  mask-image: linear-gradient(
    to top, transparent 0%, rgba(0, 0, 0, 1) 15%,
    rgba(0, 0, 0, 1) 85%, transparent 100%
  ), linear-gradient(
    to left, transparent 0%, rgba(0, 0, 0, 1) 15%,
    rgba(0, 0, 0, 1) 85%, transparent 100%
  );
  /* 溢出处理：内容超出时可滚动 */
  overflow: hidden;
  /* 过渡：跟随尺寸变化平滑过渡 */
  transition: all 0.5s ease-in-out;
  /* 圆角过渡 */
  border-radius: 16px;
}

/* 按钮默认文字 */
.btn-text {
  transition: all 0.5s ease;
}

/* 遮罩层 */
.dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.mask-tip {
  color: #fff;
  font-size: 1.2rem;
  margin-top: 20px;
  opacity: 0.8;
  text-align: center;
}

.view-records-link {
  font-size: 0.9rem;
  color: #666;
  cursor: pointer;
  text-decoration: underline;
  z-index: 2;
}

.view-records-link:hover {
  color: #333;
}

.logout-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: transparent;
  border: none;
  color: #666;
  font-size: 0.9rem;
  text-decoration: underline;
  cursor: pointer;
  padding: 0;
  z-index: 3;
}

.logout-btn:hover {
  color: #333;
}
</style>