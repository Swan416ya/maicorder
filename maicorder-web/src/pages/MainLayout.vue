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
        <div class="check-btn-container" :class="{ 'dialog-expanded': isDialogOpen }" @click="handleCheckIn"
          @click.stop="preventMaskClose">
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

      
      <!-- 遮罩层 -->
      <div class="dialog-mask" v-if="isDialogOpen" @click="closeDialog">
        <p class="mask-tip">点击空白处返回</p>
      </div>
    </div>


      <FloatingToolbar
      v-model="currentSelection"
      :items="toolbarData"
      :selectable="config.selectable"
      :show-label="config.showLabel"
    />


  </BackGround>
</template>

<script setup>
import { ref, onMounted, onUnmounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import CheckInDialog from '../components/CheckInDialog.vue'
import PurpleFab from '../components/PurpleFab.vue'
import stackIcon from '@/assets/stack.svg?raw'
import logoutIcon from '@/assets/logout.svg?raw'
import LoginLayout from './LoginLayout.vue'
import BackGround from '../components/BackGround.vue'
import moreToolIcon from '@/assets/moreTool.svg?raw'


import FloatingToolbar from '@/components/FloatingToolbar.vue'
const currentSelection = ref('format_bold');
// 配置控制
const config = reactive({
  selectable: true, // 默认开启选中背景
  showLabel: true   // 默认开启文字
});
// 图标数据 (SVG 路径)
const icons = {
  bold: stackIcon,
  italic: `<svg viewBox="0 0 24 24"><path d="M10 4v3h2.21l-3.42 8H6v3h8v-3h-2.21l3.42-8H18V4z"/></svg>`,
  link: `<svg viewBox="0 0 24 24"><path d="M3.9 12c0-1.71 1.39-3.1 3.1-3.1h4V7H7c-2.76 0-5 2.24-5 5s2.24 5 5 5h4v-1.9H7c-1.71 0-3.1-1.39-3.1-3.1zM8 13h8v-2H8v2zm9-6h-4v1.9h4c1.71 0 3.1 1.39 3.1 3.1s-1.39 3.1-3.1 3.1h-4V17h4c2.76 0 5-2.24 5-5s-2.24-5-5-5z"/></svg>`,
  color: `<svg viewBox="0 0 24 24"><path d="M12 22c4.97 0 9-4.03 9-9-4.97 0-9 4.03-9 9zM5.6 10.25a2.5 2.5 0 0 0 3.92 2.06l-.02.19a2.5 2.5 0 0 0 5 0l-.02-.19a2.5 2.5 0 0 0 3.92-2.06c0-1.38-1.12-2.5-2.5-2.5-.53 0-1.01.16-1.42.44l-.08-.69h-1.84l-.08.69c-.41-.28-.89-.44-1.42-.44-1.38 0-2.5 1.12-2.5 2.5z"/></svg>`,
};

// 工具栏数据定义
const toolbarData = [
  {
    key: 'format_bold',
    label: '查看记录',
    title: 'Bold',
    icon: stackIcon,
    action: () => handleViewRecords()
  },
  {
    key: 'format_italic',
    label: '更多工具',
    title: 'Italic',
    icon: moreToolIcon,
    action: () => moreTool()
  },
  {
    key: 'insert_link',
    label: '个人主页',
    title: 'Link',
    icon: icons.link,
    // 示例：点击执行特殊逻辑
    // action: () => handleLogout()
  },
  {
    key: 'color_fill',
    label: '退出登录',
    title: 'Color',
    icon: logoutIcon,
    action: () => handleLogout()
  }
];


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
const isLogoutBtnFabExtended = ref(0)



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

  // --- 修复内接长方形计算逻辑 ---
  
  // 圆的直径（使用98%以确保边缘显示）
  const diameter = maxDialogSize.value * 0.98
  
  // 设置一个合理的内接矩形最小高度（屏幕高度的60%）
  const minHeightRatio = 0.6
  const targetMinHeight = windowHeight * minHeightRatio
  
  if (windowWidth > windowHeight) {
    // 横屏逻辑
    let height = windowHeight * 0.9
    // 保护逻辑：如果算出的高比直径还大，就限制为直径
    if (height > diameter) height = diameter

    contentHeight.value = Math.floor(height)
    contentWidth.value = Math.floor(Math.sqrt(Math.pow(diameter, 2) - Math.pow(contentHeight.value, 2)))
  } else {
    // 竖屏逻辑（手机主要模式）
    // 优先计算一个合理的高度
    let height = Math.min(windowHeight * 0.8, diameter * 0.8)
    
    // 确保高度不小于最小高度
    if (height < targetMinHeight && targetMinHeight <= diameter) {
      height = targetMinHeight
    }
    
    // 计算对应的宽度
    contentHeight.value = Math.floor(height)
    contentWidth.value = Math.floor(Math.sqrt(Math.pow(diameter, 2) - Math.pow(contentHeight.value, 2)))
    
    // 确保宽度不会太大（不超过屏幕宽度的95%）
    const maxWidth = windowWidth * 0.95
    if (contentWidth.value > maxWidth) {
      contentWidth.value = Math.floor(maxWidth)
      // 根据宽度重新计算高度
      contentHeight.value = Math.floor(Math.sqrt(Math.pow(diameter, 2) - Math.pow(contentWidth.value, 2)))
    }
  }
  
  console.log('屏幕:', windowWidth, 'x', windowHeight, 
              '圆直径:', maxDialogSize.value, 
              '内接矩形:', contentWidth.value, 'x', contentHeight.value)
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

const preventMaskClose = () => { }

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

const moreTool = () => {
  router.push('/moretoolpage')
}
</script>

<style scoped>

/* 样式保持不变，核心布局逻辑未变 */
.main-page-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  /* background-color: #fafafa; */
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
  margin-top: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  /* 保持间距，移除时钟后按钮和下方链接会靠得更近一点，视觉上更紧凑 */
  z-index: 2;
}

.check-btn-container {
  width: 240px;  /* 从200px改为240px */
  height: 240px;  /* 从200px改为240px */
  border-radius: 50%;
  background-color: #000;
  color: #fff;
  font-size: 2.2rem;  /* 稍微增大字体 */
  font-weight: 600;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.5s ease-in-out;
  cursor: pointer;
  overflow: hidden;
  position: relative;
}

.check-btn-container.dialog-expanded {
  background-color: #fff;
  cursor: default;
  width: v-bind(dialogSize + 'px');
  height: v-bind(dialogSize + 'px');
}

/* 动态内接长方形容器 */
.dialog-content-wrapper {
  /* 布局 */
  display: flex;
  justify-content: center;
  align-items: flex-start;
  box-sizing: border-box;

  /* 尺寸 */
  width: 100%;
  height: 100%;

  /* 
    【关键调整】：
    上下 Padding 改为 30px。
    如果 Padding 太大（比如 50px），而渐变也是 50px，
    那渐变就刚好发生在空白处，文字出来时已经是黑色的了，就没有渐变感。
  */
  padding: 30px 10px;

  /* 滚动设置 */
  overflow-y: auto;
  overflow-x: hidden;

  /* 
    【核心渐变逻辑】：
    0% - 5%:   完全透明（纯白区域，保证边缘绝对干净）
    5% - 20%:  从透明渐变到显示（羽化区）
    20% - 80%: 内容完全可见
    80% - 95%: 从显示渐变到透明（羽化区）
    95% - 100%: 完全透明（纯白区域）
  */
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

  /* 确保没有其他遮罩合成模式干扰 */
  -webkit-mask-composite: source-over;
  mask-composite: add;

  /* 过渡 */
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
}

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