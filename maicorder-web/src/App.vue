<script setup>
import { ref } from 'vue'
import axios from 'axios'
import logoImg from '@/assets/logo.png' 

const API_BASE = import.meta.env.PROD ? '/api' : 'http://localhost:8080/api'
const emit = defineEmits(['login-success'])

const isRegisterMode = ref(false)
const authForm = ref({ username: '', password: '', email: '' })

// 生成右侧装饰用的黑块 (延伸到屏幕外)
const tailBlocks = Array.from({ length: 30 })

// 生成中间连接用的黑块 (固定6个)
const connectorBlocks = Array.from({ length: 12 })

const handleAuth = async () => { /* ...逻辑保持不变... */ }
</script>

<template>
  <div class="layout-container">
    
    <!-- 1. 背景网格 -->
    <div class="grid-bg"></div>

    <!-- 2. Logo -->
    <div class="logo-box anim-fade-in">
      <img :src="logoImg" alt="LOGO" />
    </div>

    <!-- 3. ★ 新增：中间连接线 (6个黑格子) ★ -->
    <div class="connector-bar">
      <div 
        v-for="(n, i) in connectorBlocks" 
        :key="i" 
        class="black-block"
        :style="{ animationDelay: `${0.4 + (i * 0.05)}s` }"
      ></div>
    </div>

    <!-- 4. 表单区域 -->
    <div class="form-layer">
      
      <div class="input-stack anim-pop-out" :class="{ 'register-mode': isRegisterMode }">
        <div class="grid-input username-pos">
          <input v-model="authForm.username" type="text" placeholder="USERNAME" />
        </div>
        <div class="grid-input password-pos">
          <input v-model="authForm.password" type="password" placeholder="PASSWORD" />
        </div>
        <div v-if="isRegisterMode" class="grid-input email-pos">
          <input v-model="authForm.email" type="text" placeholder="EMAIL" />
        </div>
      </div>

      <div class="login-btn-wrapper anim-btn-fade">
        <button @click="handleAuth" class="grid-btn">
          {{ isRegisterMode ? 'REGISTER' : 'LOGIN' }}
        </button>
        <div class="switch-text" @click="isRegisterMode = !isRegisterMode">
          {{ isRegisterMode ? '>> 返回登录' : '>> 注册新账号' }}
        </div>
      </div>

    </div>

    <!-- 中间连接线 (6个黑格子) -->
    <div class="connector-bar">
      <div 
        v-for="(n, i) in connectorBlocks" 
        :key="i" 
        class="connector-block"   <!-- 改成 connector-block -->
        :style="{ animationDelay: `${0.4 + (i * 0.05)}s` }"
      ></div>
    </div>

    <!-- 右侧装饰尾巴 -->
    <div class="black-bar-tail">
      <div 
        v-for="(n, i) in tailBlocks" 
        :key="i" 
        class="tail-block"        <!-- 改成 tail-block -->
        :style="{ animationDelay: `${1.9 + (i * 0.07)}s` }"   <!-- 更晚、更慢 -->
      ></div>
    </div>

  </div>
</template>

<style>
body { margin: 0; padding: 0; width: 100vw; height: 100vh; overflow: hidden; background-color: #fff; }
</style>

<style scoped>
/* ================= 核心参数 ================= */
.layout-container[data-v-7a7a37b1] {
  --base-padding: 4vw;                    /* 左/上安全边距，建议 3~6vw */
  --logo-max: 480px;                      /* 防止超大屏 logo 太大 */
  --logo-size: min(38vh, var(--logo-max));
  --cell-size: calc(var(--logo-size) / 4);

  --logo-left: var(--base-padding);
  --logo-top: calc((100vh - var(--logo-size)) / 2);

  --connector-length: calc(6 * var(--cell-size));
  --form-left: calc(var(--logo-left) + var(--logo-size) + var(--connector-length));
  --form-width: calc(var(--cell-size) * 5 + 1px);  /* 更保守，扣掉潜在 padding/border 影响 */

  --grid-color: #ccc;
  --theme-black: #000;

  position: absolute;
  inset: 0;
  width: 100vw;
  height: 100vh;
  margin: 0;
  padding: 0;
  overflow: hidden;
  box-sizing: border-box;
}

/* ... 动画 Keyframes 保持不变 ... */
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes blockEnter { 0% { opacity: 0; transform: scale(0.5); } 100% { opacity: 1; transform: scale(1); } }
@keyframes popOut { 0% { opacity: 0; transform: translateY(50px); } 100% { opacity: 1; transform: translateY(0); } }

.anim-btn-fade {
  animation: fadeIn 0.0s ease-out 0.5s backwards;
}
/* 按钮出现时间推迟到连接线走完 */
.anim-btn-fade { animation: fadeIn 0.5s ease-out 1.2s backwards; }
.anim-pop-out { animation: popOut 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 1.4s backwards; }

/* 通用黑块基础样式（宽高、背景色等） */
.connector-block,
.tail-block {
  width: var(--cell-size);
  height: var(--cell-size);
  background-color: var(--theme-black);
}

/* 中间连接块：出现较快、节奏紧凑 */
.connector-block {
  animation: blockEnter 0.35s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
}

/* 右侧尾巴块：出现更慢、更有“生长”感，可以微调动画 */
.tail-block {
  animation: blockEnter 0.45s cubic-bezier(0.22, 0.61, 0.36, 1) forwards;
  /* 可选：加一点层次感 */
  opacity: 0.92;  /* 稍微比中间的暗一点 */
  /* 或加轻微阴影/发光（视设计喜好） */
  /* box-shadow: 0 0 6px rgba(0,0,0,0.5); */
}

/* ... 网格与Logo样式保持不变 ... */
.grid-bg {
  position: absolute; top: 0; left: 0; right: 0; bottom: 0; z-index: 0; pointer-events: none;
  background-size: var(--cell-size) var(--cell-size);
  background-image: linear-gradient(to right, var(--grid-color) 1px, transparent 1px), linear-gradient(to bottom, var(--grid-color) 1px, transparent 1px);
  /* 对齐核心：以 Logo 左上角为原点 */
  background-position: var(--logo-left) var(--logo-top);
}

.logo-box[data-v-7a7a37b1] {
  position: absolute;
  z-index: 10;
  width: var(--logo-size);
  height: var(--logo-size);
  left: var(--logo-left);                     /* 只这一行 */
  top: var(--logo-top);
  background-color: #fff;
  box-sizing: content-box;
  border: 1px solid var(--grid-color);
  display: flex;
}
.logo-box img { width: 100%; height: 100%; object-fit: fill; display: block; }

/* ================= 布局组件 ================= */

/* ★ 中间连接线 (Connector) ★ */
.connector-bar {
  position: absolute; z-index: 10;
  display: flex;
  
  /* 起点：Logo 右侧 */
  left: calc(var(--logo-left) + var(--logo-size));
  /* 高度：Logo 底部 - 1个格子 (即第4行) */
  top: calc(var(--logo-top) + (3 * var(--cell-size)));
  
  /* 宽度：刚好6个格子，虽然 flex 会自动撑开，但写死更安全 */
  width: calc(6 * var(--cell-size));
  height: var(--cell-size);
}

/* ★ 表单层 (Form) ★ */
.form-layer {
  position: absolute; z-index: 20;
  /* 位置：使用了新定义的 --form-left */
  left: var(--form-left);
  top: var(--logo-top);
  width: var(--form-width);
  height: 100vh; pointer-events: none;
}

/* ... 输入框与按钮样式保持不变 ... */
.input-stack { position: absolute; top: 0; left: 0; width: 100%; height: 100%; pointer-events: auto; }
.grid-input {
  position: absolute; left: 0; width: 100%; height: var(--cell-size);
  background: #fff; border: 3px solid var(--theme-black); box-sizing: border-box;
  display: flex; align-items: center; transition: top 0.4s cubic-bezier(0.22, 1, 0.36, 1);
}
.grid-input input { width: 100%; height: 100%; border: none; outline: none; font-family: 'Courier New', monospace; font-weight: bold; font-size: 1.2rem; padding-left: 15px; color: var(--theme-black); }
.login-btn-wrapper {
  position: absolute; left: 0; width: 100%; height: var(--cell-size); pointer-events: auto;
  /* 按钮也在第4行 */
  top: calc(var(--cell-size) * 3);
}
.grid-btn {
  width: 100%; height: 100%; background-color: var(--theme-black); color: #fff;
  font-family: 'Courier New', sans-serif; font-weight: 900; font-size: 1.5rem; letter-spacing: 2px;
  cursor: pointer; border: none; box-sizing: border-box; border: 3px solid #fff; 
}
.grid-btn:hover { background-color: #333; }
.switch-text { position: absolute; top: 105%; right: 0; font-size: 0.8rem; font-weight: bold; color: #666; cursor: pointer; text-decoration: underline; }
.switch-text:hover { color: #000; }

.password-pos { top: calc(var(--cell-size) * 1); }
.username-pos { top: calc(var(--cell-size) * -1); }
.register-mode .email-pos { top: calc(var(--cell-size) * 1); }
.register-mode .password-pos { top: calc(var(--cell-size) * -1); }
.register-mode .username-pos { top: calc(var(--cell-size) * -3); }

/* ★ 右侧尾巴 (Tail) ★ */
.black-bar-tail {
  position: absolute; z-index: 10;
  display: flex;
  left: calc(var(--form-left) + var(--form-width));
  /* 如果想让尾巴填满右侧，可加 */
  width: calc(100vw - var(--form-left) - var(--form-width) + 20px);
  /* 高度同上 */
  top: calc(var(--logo-top) + (3 * var(--cell-size)));
}
</style>