<script setup>
import { ref } from 'vue'
import axios from 'axios'
import logoImg from '@/assets/logo.png' 

// 接口基础路径：生产环境/开发环境区分
const API_BASE = import.meta.env.PROD ? '/api' : 'http://localhost:8080/api'

// 派发登录成功事件给父组件
const emit = defineEmits(['login-success'])

// 切换登录/注册模式
const isRegisterMode = ref(false)

// 表单数据绑定
const authForm = ref({ 
  username: '', 
  password: '', 
  email: '' 
})

// 10 格连接线对应 10 个黑块（保留之前的修改）
const tailBlocks = Array.from({ length: 40 })
const connectorBlocks = Array.from({ length: 15 })

// 登录/注册核心方法（占位，需补充实际业务逻辑）
const handleAuth = async () => { 
  try {
    console.log('当前模式：', isRegisterMode.value ? '注册' : '登录')
    console.log('表单数据：', authForm.value)
  } catch (error) {
    console.error('授权请求失败：', error)
  }
}
</script>

<template>
  <div class="login-layout-container">
    <!-- 1. 背景网格（从 Logo 左侧外 10 格开始渲染） -->
    <div class="grid-bg"></div>

    <!-- 2. Logo 展示（贴左对齐） -->
    <div class="logo-box anim-fade-in">
      <img :src="logoImg" alt="系统Logo" />
    </div>

    <!-- 3. 中间连接装饰方块（10 格） -->
    <div class="connector-bar">
      <div 
        v-for="(n, i) in connectorBlocks" 
        :key="i" 
        class="black-block"
        :style="{ animationDelay: `${0.4 + (i * 0.05)}s` }"
      ></div>
    </div>

    <!-- 4. 登录/注册表单区域 -->
    <div class="form-layer">
      <div class="input-stack anim-pop-out" :class="{ 'register-mode': isRegisterMode }">
        <!-- 用户名输入框 -->
        <div class="grid-input username-pos">
          <input v-model="authForm.username" type="text" placeholder="USERNAME" />
        </div>
        <!-- 密码输入框 -->
        <div class="grid-input password-pos">
          <input v-model="authForm.password" type="password" placeholder="PASSWORD" />
        </div>
        <!-- 注册模式专属：邮箱输入框 -->
        <div v-if="isRegisterMode" class="grid-input email-pos">
          <input v-model="authForm.email" type="text" placeholder="EMAIL" />
        </div>
      </div>

      <!-- 操作按钮区域 -->
      <div class="login-btn-wrapper anim-btn-fade">
        <button @click="handleAuth" class="grid-btn">
          {{ isRegisterMode ? 'REGISTER' : 'LOGIN' }}
        </button>
        <!-- 切换登录/注册模式 -->
        <div class="switch-text" @click="isRegisterMode = !isRegisterMode">
          {{ isRegisterMode ? '&gt;&gt; 返回登录' : '&gt;&gt; 注册新账号' }}
        </div>
      </div>
    </div>

    <!-- 5. 右侧尾部装饰方块 -->
    <div class="black-bar-tail">
      <div 
        v-for="(n, i) in tailBlocks" 
        :key="i" 
        class="black-block"
        :style="{ animationDelay: `${1.5 + (i * 0.04)}s` }"
      ></div>
    </div>
  </div>
</template>

<style scoped>
/* ================= 全局变量配置 ================= */
.login-layout-container {
  /* Logo 高度：25vh（屏幕高度的1/4），可按需调整 */
  --login-logo-size: 25vh; 
  /* 装饰方块大小：Logo高度 / 4 */
  --login-cell-size: calc(var(--login-logo-size) / 4);
  
  /* Logo 贴左，无左侧空白 */
  --guide-x: 0vw; 
  --logo-left: var(--guide-x);
  --logo-top: calc((100vh - var(--login-logo-size)) / 2);
  
  /* 保留 10 格距离配置 */
  --form-width: calc(var(--login-cell-size) * 5);
  --connector-length: calc(10 * var(--login-cell-size));
  --form-left: calc(var(--logo-left) + var(--login-logo-size) + var(--connector-length));
  
  /* 颜色配置 */
  --grid-color: #ccc;
  --theme-black: #000;

  width: 100vw;
  height: 100vh;
  position: relative;
  margin: 0;
  padding: 0;
  /*overflow: hidden;*/
}

/* ================= 动画关键帧定义 ================= */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes blockEnter {
  0% { opacity: 0; transform: scale(0.5); }
  100% { opacity: 1; transform: scale(1); }
}

@keyframes popOut {
  0% { opacity: 0; transform: translateY(50px); }
  100% { opacity: 1; transform: translateY(0); }
}

/* ================= 通用动画类 ================= */
.anim-fade-in {
  animation: fadeIn 0.8s ease-out backwards;
}

.anim-btn-fade {
  animation: fadeIn 0.5s ease-out 1.2s backwards;
}

.anim-pop-out {
  animation: popOut 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 1.4s backwards;
}

/* ================= 布局组件样式（核心修正：网格从 Logo 左侧外 10 格开始） ================= */
/* 背景网格（★ 核心修正：水平起始位置 = Logo左侧 - 10个网格单元，垂直与Logo顶部对齐） */
.grid-bg {
  position: absolute;
  top: 0;
  left: calc(var(--logo-left) - 10 * var(--login-cell-size));
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
  background-size: var(--login-cell-size) var(--login-cell-size);
  background-image: 
    linear-gradient(to right, var(--grid-color) 1px, transparent 1px),
    linear-gradient(to bottom, var(--grid-color) 1px, transparent 1px);
  background-position: 0 var(--logo-top);
}

/* Logo 容器（贴左，无额外偏移） */
.logo-box {
  position: absolute;
  z-index: 10;
  width: var(--login-logo-size);
  height: var(--login-logo-size);
  left: var(--logo-left);
  top: var(--logo-top);
  background-color: #fff;
  box-sizing: content-box;
  border: 1px solid var(--grid-color);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-box img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

/* 中间连接装饰条（10 格宽度） */
.connector-bar {
  position: absolute;
  z-index: 15;
  display: flex;
  left: calc(var(--logo-left) + var(--login-logo-size));
  top: calc(var(--logo-top) + (3 * var(--login-cell-size)));
  width: var(--connector-length);
  height: var(--login-cell-size);
}

/* 右侧尾部装饰条 */
.black-bar-tail {
  position: absolute;
  z-index: 10;
  display: flex;
  left: calc(var(--form-left) + var(--form-width));
  top: calc(var(--logo-top) + (3 * var(--login-cell-size)));
}

/* 装饰方块 */
.black-block {
  width: var(--login-cell-size);
  height: var(--login-cell-size);
  background-color: var(--theme-black);
  animation: blockEnter 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275) backwards;
}

/* 表单层容器（左对齐，无左侧空白） */
.form-layer {
  position: absolute;
  z-index: 20;
  left: var(--form-left);
  top: var(--logo-top);
  width: var(--form-width);
  height: 100vh;
  pointer-events: none;
}

/* 输入框堆叠容器 */
.input-stack {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: auto;
}

/* 输入框样式 */
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

.grid-input input:focus {
  color: #333;
}

/* 登录/注册按钮容器 */
.login-btn-wrapper {
  position: absolute;
  left: 0;
  width: 100%;
  height: var(--login-cell-size);
  pointer-events: auto;
  top: calc(var(--login-cell-size) * 3);
}

/* 核心操作按钮 */
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

.grid-btn:hover {
  background-color: #333;
}

/* 切换模式文本 */
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

.switch-text:hover {
  color: var(--theme-black);
}

/* 输入框位置控制 */
.password-pos {
  top: calc(var(--login-cell-size) * 1);
}

.username-pos {
  top: calc(var(--login-cell-size) * -1);
}

.register-mode .email-pos {
  top: calc(var(--login-cell-size) * 1);
}

.register-mode .password-pos {
  top: calc(var(--login-cell-size) * -1);
}

.register-mode .username-pos {
  top: calc(var(--login-cell-size) * -3);
}
</style>