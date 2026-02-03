<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import logoImg from '@/assets/logo.png' 

// 接口基础路径：生产环境/开发环境区分
const API_BASE = import.meta.env.PROD ? '/api' : 'http://localhost:8080/api'

// 路由实例
const router = useRouter()

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

// 加载状态（防止重复点击）
const isLoading = ref(false)

// 装饰块数量配置
const tailBlocks = Array.from({ length: 40 })
const connectorBlocks = Array.from({ length: 15 })

const handleAuth = async () => { 
  console.log('===== 按钮点击成功，进入登录方法 =====')

  // 表单基础验证
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
      // 注册逻辑
      response = await axios.post(`${API_BASE}/register`, {
        username: authForm.value.username,
        password: authForm.value.password,
        email: authForm.value.email
      })
      console.log('注册成功！请登录')
      isRegisterMode.value = false
      authForm.value = { username: '', password: '', email: '' }
    } else {
      // 登录逻辑
      response = await axios.post(`${API_BASE}/login`, {
        username: authForm.value.username,
        password: authForm.value.password
      })
      
      const result = response.data;
      if (result.code === 200) {
        const businessData = result.data;
        if (businessData.token) {
          // 存储token和用户信息
          localStorage.setItem('token', businessData.token);
          localStorage.setItem('currentUser', JSON.stringify(businessData.user)); // 关键行
          axios.defaults.headers.common['Authorization'] = `Bearer ${businessData.token}`;
          emit('login-success', businessData.user);
          console.log('登录成功！跳转至主页面');
          await router.push('/main');
        } else {
          console.error('登录失败：后端未返回token');
          alert('登录失败：系统异常');
        }
      } else {
        console.error('登录失败：', result.message);
        alert(result.message || '登录失败');
      }
    }
  } catch (error) {
    let errMsg = '操作失败，请稍后重试'
    if (error.response) {
      errMsg = error.response.data?.message || `请求失败：${error.response.status}`
    } else if (error.request) {
      errMsg = '网络异常，请检查后端服务是否启动！'
    } else {
      errMsg = `请求异常：${error.message}`
    }
    console.error('授权请求失败：', errMsg)
    alert(errMsg)
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="login-layout-container">
    <!-- 1. 背景网格 -->
    <div class="grid-bg"></div>

    <!-- 2. Logo 展示 -->
    <div class="logo-box anim-fade-in">
      <img :src="logoImg" alt="系统Logo" />
    </div>

    <!-- 3. 中间连接装饰方块 -->
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
        <button @click="handleAuth" class="grid-btn" :disabled="isLoading">
          {{ isLoading ? (isRegisterMode ? 'REGISTERING...' : 'LOGGING IN...') : (isRegisterMode ? 'REGISTER' : 'LOGIN') }}
        </button>
        <!-- 切换登录/注册模式 -->
        <div class="switch-text" @click="isRegisterMode = !isRegisterMode" v-if="!isLoading">
          {{ isRegisterMode ? '&gt;&gt; 返回登录' : '&gt;&gt; 注册新账号' }}
        </div>
      </div>
    </div>

    <!-- 5. 右侧尾部装饰条 -->
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
/* 全局变量配置 */
.login-layout-container {
  --login-logo-size: 25vh; 
  --login-cell-size: calc(var(--login-logo-size) / 4);
  
  --guide-x: 15vw; 
  --logo-left: var(--guide-x);
  --logo-top: calc((100vh - var(--login-logo-size)) / 2);
  
  --form-width: calc(var(--login-cell-size) * 5);
  --connector-length: calc(10 * var(--login-cell-size));
  --form-left: calc(var(--logo-left) + var(--login-logo-size) + var(--connector-length));
  
  --grid-color: #ccc;
  --theme-black: #000;

  width: 100vw;
  height: 100vh;
  position: relative;
  margin: 0;
  padding: 0;
}

/* 动画关键帧定义 */
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

/* 通用动画类 */
.anim-fade-in {
  animation: fadeIn 0.8s ease-out backwards;
}

.anim-btn-fade {
  animation: fadeIn 0.5s ease-out 1.2s backwards;
}

.anim-pop-out {
  animation: popOut 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 1.4s backwards;
}

/* 布局组件样式 */
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

.connector-bar {
  position: absolute;
  z-index: 15;
  display: flex;
  left: calc(var(--logo-left) + var(--login-logo-size));
  top: calc(var(--logo-top) + (3 * var(--login-cell-size)));
  width: var(--connector-length);
  height: var(--login-cell-size);
}

.black-bar-tail {
  position: absolute;
  z-index: 10;
  display: flex;
  left: calc(var(--form-left) + var(--form-width));
  top: calc(var(--logo-top) + (3 * var(--login-cell-size)));
}

.black-block {
  width: var(--login-cell-size);
  height: var(--login-cell-size);
  background-color: var(--theme-black);
  animation: blockEnter 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275) backwards;
}

.form-layer {
  position: absolute;
  z-index: 20;
  left: var(--form-left);
  top: var(--logo-top);
  width: var(--form-width);
  height: 100vh;
  pointer-events: none;
}

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

.grid-input input:focus {
  color: #333;
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

.grid-btn:disabled {
  background-color: #666;
  cursor: not-allowed;
}

.grid-btn:hover:not(:disabled) {
  background-color: #333;
}

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