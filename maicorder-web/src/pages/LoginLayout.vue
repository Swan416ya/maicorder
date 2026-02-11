<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import logoImg from '@/assets/logo.png' 

const API_BASE = import.meta.env.PROD ? '/api' : 'http://localhost:8080/api'
const router = useRouter()
const emit = defineEmits(['login-success'])

// 基础变量
const isRegisterMode = ref(false)
const authForm = ref({ username: '', password: '', email: '' })
const isLoading = ref(false)
const tailBlocks = Array.from({ length: 40 })
const connectorBlocks = Array.from({ length: 15 })
const isNarrowMode = ref(false)

// 核心修复：计算格子大小（改用Logo实际尺寸，避免CSS变量获取失败）
const getCellSize = () => {
  const logoBox = document.querySelector('.logo-box')
  if (!logoBox) return 60 // 兜底默认值（px）
  const logoSize = logoBox.offsetWidth // 获取Logo实际宽度
  return logoSize / 4 // 格子大小 = Logo尺寸/4
}

// 修复布局判断逻辑（添加调试日志，简化计算）
const calculateLayoutMode = () => {
  // 1. 获取格子大小（优先用Logo实际尺寸，兜底60px）
  const loginCellSize = getCellSize()
  // 2. 窄屏阈值：18 * 格子大小
  const narrowThreshold = loginCellSize * 18
  // 3. 当前窗口宽度
  const currentWidth = window.innerWidth
  
  // 4. 更新窄屏状态
  isNarrowMode.value = currentWidth < narrowThreshold*1.25
  // 5. 强制添加/移除类名（确保生效）
  const container = document.querySelector('.login-layout-container')
  if (container) {
    if (isNarrowMode.value) {
      container.classList.add('narrow-mode')
    } else {
      container.classList.remove('narrow-mode')
    }
  }
}

// 新增：初始化网格中心位置（核心）
const initGridCenter = () => {
  const gridBg = document.querySelector('.grid-bg')
  if (!gridBg) return
  const gridRect = gridBg.getBoundingClientRect()
  // 计算容器中心坐标
  const centerX = gridRect.width / 2
  const centerY = gridRect.height / 2
  // 设置默认光点在中心（无需鼠标触发）
  gridBg.style.setProperty('--mouse-x', `${centerX}px`)
  gridBg.style.setProperty('--mouse-y', `${centerY}px`)
}

// 鼠标跟随逻辑（保留，鼠标移动后覆盖默认中心）
const handleMouseMove = (e) => {
  const gridBg = document.querySelector('.grid-bg')
  if (!gridBg) return
  const gridRect = gridBg.getBoundingClientRect()
  // 鼠标移动后，光点跟随鼠标（覆盖默认中心）
  gridBg.style.setProperty('--mouse-x', `${e.clientX - gridRect.left}px`)
  gridBg.style.setProperty('--mouse-y', `${e.clientY - gridRect.top}px`)
}

// 窗口resize逻辑（保留，同时重置中心位置）
const handleResize = () => {
  calculateLayoutMode()
  // resize后重新初始化中心（避免窗口变化后光点偏移）
  initGridCenter()
  // 重新触发鼠标位置计算（可选，保持兼容性）
  const mouseEvent = new MouseEvent('mousemove', {
    clientX: window.mouseX || 0,
    clientY: window.mouseY || 0
  })
  handleMouseMove(mouseEvent)
}

// 生命周期：确保DOM渲染完成后再计算（关键！）
onMounted(async () => {
  // 等待DOM完全渲染，再计算布局
  await nextTick()
  // 1. 初始化布局
  calculateLayoutMode()
  // 2. 初始化网格光点居中（核心：页面加载完成就显示中心网格）
  initGridCenter()
  // 3. 监听事件
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('resize', handleResize)
})

// 登录/注册逻辑（保留）
const handleAuth = async () => { 
  if (!authForm.value.username || !authForm.value.password) {
    console.warn('用户名和密码不能为空！')
    return
  }
  if (isRegisterMode.value && !authForm.value.email) {
    console.warn('注册邮箱不能为空！')
    return
  }

  isLoading.value = true
  try {
    let response
    axios.defaults.headers.common['Content-Type'] = 'application/json'
    if (isRegisterMode.value) {
      response = await axios.post(`${API_BASE}/register`, authForm.value)
      console.log('注册成功！请登录')
      isRegisterMode.value = false
      authForm.value = { username: '', password: '', email: '' }
    } else {
      response = await axios.post(`${API_BASE}/login`, {
        username: authForm.value.username,
        password: authForm.value.password
      })
      const result = response.data;
      if (result.code === 200 && result.data.token) {
        localStorage.setItem('token', result.data.token);
        localStorage.setItem('currentUser', JSON.stringify(result.data.user));
        localStorage.setItem('userId', result.data.user.id);
        axios.defaults.headers.common['Authorization'] = `Bearer ${result.data.token}`;
        emit('login-success', result.data.user);
        await router.push('/main');
      } else {
        alert('登录失败：' + (result.message || '系统异常'));
      }
    }
  } catch (error) {
    let errMsg = '操作失败，请稍后重试'
    if (error.response) errMsg = error.response.data?.message || `请求失败：${error.response.status}`
    else if (error.request) errMsg = '网络异常，请检查后端服务是否启动！'
    alert(errMsg)
    console.error('授权失败：', error)
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="login-layout-container">
    <!-- 背景网格 -->
    <div class="grid-bg"></div>
    <!-- Logo（无描边） -->
    <div class="logo-box anim-fade-in">
      <img :src="logoImg" alt="系统Logo" />
    </div>
    <!-- 连接装饰（窄屏隐藏） -->
    <div class="connector-bar">
      <div v-for="(n, i) in connectorBlocks" :key="i" class="black-block" :style="{ animationDelay: `${0.4 + (i * 0.05)}s` }"></div>
    </div>
    <!-- 表单区域 -->
    <div class="form-layer">
      <div class="input-stack anim-pop-out" :class="{ 'register-mode': isRegisterMode }">
        <div class="grid-input username-pos"><input v-model="authForm.username" type="text" placeholder="USERNAME" /></div>
        <div class="grid-input password-pos"><input v-model="authForm.password" type="password" placeholder="PASSWORD" /></div>
        <div v-if="isRegisterMode" class="grid-input email-pos"><input v-model="authForm.email" type="text" placeholder="EMAIL" /></div>
      </div>
      <div class="login-btn-wrapper anim-btn-fade">
        <button @click="handleAuth" class="grid-btn" :disabled="isLoading">
          {{ isLoading ? (isRegisterMode ? 'REGISTERING...' : 'LOGGING IN...') : (isRegisterMode ? 'REGISTER' : 'LOGIN') }}
        </button>
        <div class="switch-text" @click="isRegisterMode = !isRegisterMode" v-if="!isLoading">
          {{ isRegisterMode ? '&gt;&gt; 返回登录' : '&gt;&gt; 注册新账号' }}
        </div>
      </div>
    </div>
    <!-- 尾部装饰（窄屏隐藏） -->
    <div class="black-bar-tail">
      <div v-for="(n, i) in tailBlocks" :key="i" class="black-block" :style="{ animationDelay: `${1.5 + (i * 0.04)}s` }"></div>
    </div>
  </div>
</template>

<style scoped>
.login-layout-container {
  --login-logo-size: 25vh; 
  --login-cell-size: calc(var(--login-logo-size) / 4);
  --grid-radius: calc(var(--login-cell-size) * 7);
  --grid-feather: calc(var(--login-cell-size) * 1);
  
  --guide-x: 15vw; 
  --logo-left: var(--guide-x);
  --logo-top: calc((100vh - var(--login-logo-size)) / 2);
  
  --form-width: calc(var(--login-cell-size) * 5);
  --connector-length: calc(10 * var(--login-cell-size));
  --form-left: calc(var(--logo-left) + var(--login-logo-size) + var(--connector-length));
  
  --grid-color: #ccc;
  --theme-black: #000;

  width: 100vw !important; /* 强制占满视口宽度 */
  height: 100vh !important;
  position: relative;
  margin: 0 !important;
  padding: 0 !important;
  overflow: hidden;
  transition: all 0.3s ease;
}

/* 窄屏模式样式（强制生效）- 表单整体下移2格 */
.login-layout-container.narrow-mode {
  --logo-left: calc((100vw - var(--login-logo-size)) / 2) !important;
  --logo-top: calc(20vh - var(--login-logo-size) / 2) !important;
  --form-left: calc((100vw - var(--form-width)) / 2) !important;
  --form-top: calc(var(--logo-top) + var(--login-logo-size) + var(--login-cell-size) * 4) !important;
}

/* 动画 */
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes blockEnter { 0% { opacity: 0; transform: scale(0.5); } 100% { opacity: 1; transform: scale(1); } }
@keyframes popOut { 0% { opacity: 0; transform: translateY(50px); } 100% { opacity: 1; transform: translateY(0); } }

.anim-fade-in { animation: fadeIn 0.8s ease-out backwards; }
.anim-btn-fade { animation: fadeIn 0.5s ease-out 1.2s backwards; }
.anim-pop-out { animation: popOut 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 1.4s backwards; }

/* 网格背景 - 核心修改：默认显示（无需hover），初始光点居中 */
.grid-bg {
  position: absolute;
  top: 0;
  left: calc(var(--logo-left) - 10 * var(--login-cell-size));
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
  background-size: var(--login-cell-size) var(--login-cell-size);
  background-image: linear-gradient(to right, var(--grid-color) 1px, transparent 1px),
                    linear-gradient(to bottom, var(--grid-color) 1px, transparent 1px);
  background-position: 0 var(--logo-top);
  opacity: 1 !important; /* 核心：默认显示网格，无需鼠标hover */
  -webkit-mask-image: radial-gradient(
    circle at var(--mouse-x, 50%) var(--mouse-y, 50%), /* 兜底：没有鼠标值时居中 */
    white calc(var(--grid-radius) - var(--grid-feather)),
    rgba(255,255,255,0.5) var(--grid-radius),
    transparent calc(var(--grid-radius) + var(--grid-feather))
  );
  mask-image: radial-gradient(
    circle at var(--mouse-x, 50%) var(--mouse-y, 50%), /* 兜底：没有鼠标值时居中 */
    white calc(var(--grid-radius) - var(--grid-feather)),
    rgba(255,255,255,0.5) var(--grid-radius),
    transparent calc(var(--grid-radius) + var(--grid-feather))
  );
  -webkit-mask-size: 100% 100%;
  mask-size: 100% 100%;
  transition: opacity 0.1s ease;
}

/* 保留：鼠标hover时网格亮度不变（可选，也可删除） */
.login-layout-container:hover .grid-bg { opacity: 1; }

/* Logo（无描边） */
.logo-box {
  position: absolute;
  z-index: 10;
  width: var(--login-logo-size);
  height: var(--login-logo-size);
  left: var(--logo-left);
  top: var(--logo-top);
  background-color: #fff;
  box-sizing: content-box;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: left 0.3s ease, top 0.3s ease;
}

.logo-box img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  border: none !important;
  outline: none !important;
}

/* 装饰条（窄屏隐藏） */
.connector-bar, .black-bar-tail {
  position: absolute;
  z-index: 15;
  display: flex;
  left: calc(var(--logo-left) + var(--login-logo-size));
  top: calc(var(--logo-top) + (3 * var(--login-cell-size)));
  width: var(--connector-length);
  height: var(--login-cell-size);
  transition: opacity 0.3s ease, left 0.3s ease;
}

.black-bar-tail {
  z-index: 10;
  left: calc(var(--form-left) + var(--form-width));
  width: auto;
}

.login-layout-container.narrow-mode .connector-bar,
.login-layout-container.narrow-mode .black-bar-tail {
  opacity: 0 !important;
  pointer-events: none;
}

.black-block {
  width: var(--login-cell-size);
  height: var(--login-cell-size);
  background-color: var(--theme-black);
  animation: blockEnter 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275) backwards;
}

/* 表单区域 */
.form-layer {
  position: absolute;
  z-index: 20;
  left: var(--form-left);
  top: var(--form-top, var(--logo-top));
  width: var(--form-width);
  height: auto;
  min-height: calc(var(--login-cell-size) * 4);
  pointer-events: none;
  transition: left 0.3s ease, top 0.3s ease;
}

.login-layout-container.narrow-mode .form-layer { height: auto; }

.input-stack {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: auto;
}

.grid-input {
  position: absolute;
  left: 0;
  width: 100%;
  height: var(--login-cell-size);
  background: #fff;
  border: 3px solid var(--theme-black);
  box-sizing: border-box;
  display: flex;
  align-items: center;
  transition: top 0.4s cubic-bezier(0.22, 1, 0.36, 1);
}

.grid-input input {
  width: 100%;
  height: 100%;
  border: none;
  outline: none;
  font-family: 'Courier New', monospace;
  font-weight: bold;
  font-size: 1.2rem;
  padding-left: 15px;
  color: var(--theme-black);
  background: transparent;
}

.login-btn-wrapper {
  position: absolute;
  left: 0;
  width: 100%;
  height: var(--login-cell-size);
  pointer-events: auto;
  top: calc(var(--login-cell-size) * 3);
}

.grid-btn {
  width: 100%;
  height: 100%;
  background-color: var(--theme-black);
  color: #fff;
  font-family: 'Courier New', monospace;
  font-weight: 900;
  font-size: 1.5rem;
  letter-spacing: 2px;
  cursor: pointer;
  border: none;
  box-sizing: border-box;
  border: 3px solid #fff;
  transition: background-color 0.3s ease;
}

.grid-btn:disabled { background-color: #666; cursor: not-allowed; }
.grid-btn:hover:not(:disabled) { background-color: #333; }

.switch-text {
  position: absolute;
  top: 105%;
  right: 0;
  font-size: 0.8rem;
  font-weight: bold;
  color: #666;
  cursor: pointer;
  text-decoration: underline;
  transition: color 0.3s ease;
}

.switch-text:hover { color: var(--theme-black); }

/* 输入框位置 */
.password-pos { top: calc(var(--login-cell-size) * 1); }
.username-pos { top: calc(var(--login-cell-size) * -1); }
.register-mode .email-pos { top: calc(var(--login-cell-size) * 1); }
.register-mode .password-pos { top: calc(var(--login-cell-size) * -1); }
.register-mode .username-pos { top: calc(var(--login-cell-size) * -3); }

/* 小屏幕高度适配 */
@media (max-height: 600px) {
  .login-layout-container { --login-logo-size: 20vh; }
  .login-layout-container.narrow-mode {
    --logo-top: calc(10vh - var(--login-logo-size) / 2);
    --form-top: calc(var(--logo-top) + var(--login-logo-size) + var(--login-cell-size));
  }
}
</style>