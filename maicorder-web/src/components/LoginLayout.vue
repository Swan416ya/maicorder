<script setup>
import { ref } from 'vue'
import axios from 'axios'
import logoImg from '@/assets/logo.png' // 注意这里的 @ 也是指向 src 目录

const API_BASE = import.meta.env.PROD ? '/api' : 'http://localhost:8080/api'

// 定义事件：登录成功后通知父组件
const emit = defineEmits(['login-success'])

const isRegisterMode = ref(false)
const authForm = ref({ username: '', password: '', email: '' })

const handleAuth = async () => {
  if (!authForm.value.username || !authForm.value.password) return alert("账号密码不能为空")
  
  try {
    const url = isRegisterMode.value ? `${API_BASE}/users` : `${API_BASE}/login`
    const res = await axios.post(url, authForm.value)
    
    if (res.data.success) {
      if (isRegisterMode.value) {
        alert("注册成功！请登录")
        isRegisterMode.value = false
      } else {
        // 登录成功，把用户信息“发射”给父组件 App.vue
        emit('login-success', res.data.user)
      }
    } else {
      alert("操作失败：" + res.data.message)
    }
  } catch (e) {
    console.error(e)
    alert("连接服务器失败")
  }
}
</script>

<template>
  <div class="design-login-page">
    <!-- 背景网格 -->
    <div class="grid-bg"></div>

    <div class="content-wrapper">
      <!-- 左侧：Logo区域 -->
      <div class="logo-section">
        <div class="logo-box">
          <img :src="logoImg" alt="勤了吗 Logo" />
        </div>
        <div class="black-line"></div>
      </div>

      <!-- 右侧：表单区域 -->
      <div class="form-section">
        <div class="form-box">
          <h2 class="form-title">
            {{ isRegisterMode ? 'REGISTER' : 'LOGIN' }}
            <span class="sub-text">{{ isRegisterMode ? '注册账号' : '身份验证' }}</span>
          </h2>

          <div class="input-group">
            <label>USERNAME</label>
            <input v-model="authForm.username" type="text" />
          </div>

          <div class="input-group">
            <label>PASSWORD</label>
            <input v-model="authForm.password" type="password" />
          </div>

          <div class="input-group" v-if="isRegisterMode">
            <label>EMAIL</label>
            <input v-model="authForm.email" type="text" placeholder="Optional" />
          </div>

          <div class="action-group">
            <button @click="handleAuth" class="pixel-btn">
              {{ isRegisterMode ? '注册 / REGISTER' : '登录 / ENTER' }}
            </button>
            
            <div class="switch-link" @click="isRegisterMode = !isRegisterMode">
              {{ isRegisterMode ? '<< 返回登录' : '注册新账号 >>' }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 这里的样式只对当前组件生效 */
.design-login-page {
  width: 100vw; height: 100vh;
  position: relative; overflow: hidden;
  background-color: #fff;
  display: flex; align-items: center; justify-content: center;
}

/* 使用 var() 调用 App.vue 里定义的全局变量 */
.grid-bg {
  position: absolute; top: 0; left: 0; right: 0; bottom: 0; z-index: 0;
  background-size: var(--cell-size) var(--cell-size);
  background-image:
    linear-gradient(to right, var(--grid-color) 1px, transparent 1px),
    linear-gradient(to bottom, var(--grid-color) 1px, transparent 1px);
}

.content-wrapper {
  position: relative; z-index: 10;
  display: flex; width: 100%; max-width: 1400px; height: 600px;
  align-items: center;
}

.logo-section {
  position: relative;
  width: calc(var(--cell-size) * 4);
  height: calc(var(--cell-size) * 4);
  margin-left: calc(var(--cell-size) * 2);
  flex-shrink: 0;
}

.logo-box { width: 100%; height: 100%; background: #000; }
.logo-box img { width: 100%; height: 100%; object-fit: stretch; display: block; }

.black-line {
  position: absolute;
  height: var(--cell-size);
  background-color: var(--theme-black);
  bottom: 0; left: 100%; width: 200vw; pointer-events: none;
}

.form-section {
  flex: 1; display: flex; justify-content: center; align-items: center;
  position: relative; transform: translateY(-40px);
}

.form-box {
  width: 360px; background: #fff;
  border: 4px solid var(--theme-black);
  padding: 40px;
  box-shadow: 10px 10px 0 rgba(0,0,0,0.1);
  z-index: 20;
}

.form-title { font-size: 2rem; font-weight: 900; margin: 0 0 30px 0; letter-spacing: -1px; line-height: 1; }
.sub-text { display: block; font-size: 0.9rem; font-weight: normal; color: #666; margin-top: 5px; letter-spacing: 0; }

.input-group { margin-bottom: 20px; }
.input-group label { display: block; font-size: 0.8rem; font-weight: bold; margin-bottom: 5px; letter-spacing: 1px; }
.input-group input {
  width: 100%; padding: 10px; box-sizing: border-box;
  border: 2px solid #ddd; font-family: monospace; font-size: 1.1rem;
  outline: none; transition: 0.2s;
}
.input-group input:focus { border-color: var(--theme-black); background: #fafafa; }

.pixel-btn {
  width: 100%; padding: 15px; background: var(--theme-black); color: #fff;
  border: none; font-size: 1.1rem; font-weight: bold; cursor: pointer;
  margin-top: 10px; transition: 0.2s;
}
.pixel-btn:hover { background: #333; transform: translateY(-2px); box-shadow: 0 4px 0 #666; }

.switch-link { text-align: center; margin-top: 20px; font-size: 0.9rem; cursor: pointer; text-decoration: underline; color: #666; }
.switch-link:hover { color: #000; }

@media (max-width: 900px) {
  .content-wrapper { flex-direction: column; height: auto; padding-top: 50px; }
  .logo-section { margin-left: 0; margin-bottom: 40px; width: 240px; height: 240px; }
  .black-line { display: none; }
  .form-section { transform: translateY(0); width: 100%; padding: 0 20px; }
  .form-box { width: 100%; }
}
</style>