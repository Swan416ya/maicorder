<template>
  <!-- 主容器：全屏黑客风格个人资料页面 -->
  <div class="profile-container">
    <!-- 背景遮罩层：增强背景图片与内容的融合感 -->
    <div class="background-overlay"></div>

    <!-- 3D透视舞台：为变形效果提供3D空间环境 -->
    <div class="perspective-stage">
      
      <!-- 信息面板：应用3D变形效果的核心容器 -->
      <div class="info-panel-deformed">
        
        <!-- 内容包装器：承载实际显示的内容 -->
        <div class="content-wrapper">
          <!-- 终端风格标题：显示用户名，带有打字机效果和闪烁光标 -->
          <h1 class="terminal-title">
            {{ typedName }}<span class="cursor">_</span>
          </h1>
          
          <!-- 详细信息列表：遍历显示所有用户资料项 -->
          <div class="details-list">
            <!-- 遍历displayInfo对象，为每个资料项创建一个可点击的行 -->
            <div 
              v-for="(item, key) in displayInfo" 
              :key="key" 
              class="info-item"
              @click="openEdit(key)"
            >
              <!-- 标签：显示字段名的大写形式 -->
              <span class="label">> {{ key }}:</span>
              <!-- 值：显示当前字段的值（带有打字机效果） -->
              <span class="value">{{ item.visibleValue }}</span>
              <!-- 小光标：仅在打字时显示，增强终端感 -->
              <span v-if="item.isTyping" class="cursor-sm">█</span>
            </div>
          </div>
        </div>

        <!-- 扫描线效果：模拟老式CRT显示器的扫描线 -->
        <div class="scanner-line"></div>
      </div>
    </div>
    <!-- 图片展示块
    <div class="image-block">
      <div class="image-frame">
        <img src="@/assets/box.png" alt="Box Image" class="box-image" />
        <div class="image-scan-line"></div>
      </div>
      <div class="image-label">> SYSTEM_IMAGE</div>
    </div> -->

    <!-- 返回主页按钮：左下角固定位置 -->
    <div class="back-home-btn" @click="goHome">
      <span class="btn-icon"><<</span>
      <span class="btn-text">RETURN_HOME</span>
    </div>

    <!-- 编辑弹窗：使用Vue Transition实现淡入淡出动画 -->
    <Transition name="fade">
      <!-- 弹窗遮罩层：点击外部可关闭 -->
      <div v-if="isEditing" class="modal-overlay">
        <!-- 终端风格弹窗 -->
        <div class="terminal-modal">
          <!-- 弹窗标题：显示当前编辑的字段名 -->
          <div class="modal-header">MOD_PARAM: {{ currentEditKey.toUpperCase() }}</div>
          <!-- 弹窗内容区：输入框和错误提示 -->

          <div class="modal-body">
            <!-- 输入框：绑定editValue，支持回车键保存 -->
            <input 
              v-model="editValue" 
              ref="editInput"
              @keyup.enter="handleSave"
              class="terminal-input"
            />
            <!-- 错误信息：验证失败时显示 -->
            <p v-if="errorMessage" class="error-msg">!! ERROR: {{ errorMessage }}</p>
          </div>
          <!-- 弹窗底部：操作按钮 -->
          <div class="modal-footer">
            <button @click="handleSave" class="btn" :disabled="isLoading">
              {{ isLoading ? '[ 处理中... ]' : '[ 确认 ]' }}
            </button>
            <button @click="isEditing = false" class="btn" :disabled="isLoading">[ 取消 ]</button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
/**
 * TestPage.vue - 黑客风格个人资料页面
 * 
 * 核心功能：
 * 1. 3D透视变形效果：通过CSS 3D变换实现信息面板的倾斜展示
 * 2. 打字机动画：模拟终端打字效果，逐字显示内容
 * 3. 实时编辑功能：点击资料项可弹出编辑框修改内容
 * 4. 终端视觉风格：绿色主题、闪烁光标、扫描线等黑客元素
 */

import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
// import localStorage from '@/utils/localStorage';

const router = useRouter();
/**
 * 用户资料数据
 * 使用reactive创建响应式对象，存储所有用户信息的原始数据
 */
const profile = reactive({
  username: '',           // 用户名
  email: '',       // 邮箱地址
  password: '',           // 密码（掩码显示）
  uid: '',                  // 用户ID
  apikey: '',             // API密钥
  maicorderApiKey:''
});
const initProfile = async () => {

  // 从localStorage获取token
  profile.maicorderApiKey = localStorage.getItem("token") || "maicorder-api-key";
  
  // 从currentUser对象中获取用户信息
  try {
    const currentUserStr = localStorage.getItem("currentUser");
    if (currentUserStr) {
      const currentUser = JSON.parse(currentUserStr);
      profile.username = currentUser.username || "username";
      profile.email = currentUser.email || "email";
      profile.uid = currentUser.id || "UID-992831";
    } else {
      // 如果没有currentUser，使用默认值
      profile.username = "username";
      profile.email = "email";
      profile.uid = "UID-992831";
    }
  } catch (error) {
    console.error('解析currentUser失败：', error);
    // 解析失败时使用默认值
    profile.username = "username";
    profile.email = "email";
    profile.uid = "UID-992831";
  }
  
  // 密码始终使用默认的掩码值
  profile.password = "************";

  
  // 从后端获取API Key
  try {
    const token = localStorage.getItem("token");
    if (token) {
      const response = await axios.get('/api/get-apikey', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (response.data.code === 200 && response.data.data) {
        profile.apikey = response.data.data;
        // 可选：将获取的API Key存储到localStorage
        localStorage.setItem("apikey", response.data.data);
      }
      else {
        profile.apikey = response.data.message;
        // 可选：将获取的API Key存储到localStorage
        localStorage.setItem("apikey", response.data.message);
      }
    }
  } catch (error) {
    console.error('获取API Key失败：', error);
    // 失败时使用localStorage中的值或默认值
    profile.apikey = "null";
  }
}

/**
 * 展示信息对象
 * 为每个profile字段创建对应的展示对象，包含：
 * - visibleValue: 当前可见的文本（用于打字机效果）
 * - isTyping: 是否正在打字（控制光标显示）
 * 
 * 使用reduce动态生成，避免手动定义每个字段
 */
const displayInfo = reactive(Object.keys(profile).reduce((acc, key) => {
  acc[key] = { visibleValue: '', isTyping: false };
  return acc;
}, {}));

// 标题打字效果的状态变量
const typedName = ref('');

// 编辑弹窗的显示状态
const isEditing = ref(false);

// 当前正在编辑的字段键名
const currentEditKey = ref('');

// 编辑输入框的绑定值
const editValue = ref('');

// 错误提示信息
const errorMessage = ref('');

// 加载状态
const isLoading = ref(false);

// 编辑输入框的DOM引用（用于自动聚焦）
const editInput = ref(null);

/**
 * 睡眠函数
 * 返回一个Promise，在指定毫秒后resolve
 * 用于实现打字机效果的延迟
 * 
 * @param {number} ms - 延迟毫秒数
 * @returns {Promise} - 延迟后的Promise
 */
const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

/**
 * 组件挂载后的初始化
 * 1. 初始化用户信息（从后端获取API Key）
 * 2. 执行打字机动画，依次显示标题和各个字段
 */
onMounted(async () => {
  // 初始化用户信息，包括从后端获取API Key
  await initProfile();
  
  // 标题打字动画：逐字显示用户名
  for (let i = 0; i <= profile.username.length; i++) {
    typedName.value = profile.username.substring(0, i);
    await sleep(80);  // 每个字符间隔80ms
  }
  
  // 列表项打字动画：逐个字段显示
  for (const key in profile) {
    // 标记为正在打字，显示小光标
    displayInfo[key].isTyping = true;
    
    // 逐字显示当前字段的值
    const value = String(profile[key]);
    for (let i = 0; i <= value.length; i++) {
      displayInfo[key].visibleValue = value.substring(0, i);
      await sleep(10);  // 列表项打字速度更快（30ms）
    }
    
    // 打字完成，隐藏光标
    displayInfo[key].isTyping = false;
    await sleep(150);  // 字段间间隔150ms
  }
});

/**
 * 打开编辑弹窗
 * 点击资料项时触发，设置当前编辑的字段并显示弹窗
 * 
 * @param {string} key - 要编辑的字段键名
 */
const openEdit = (key) => {
  currentEditKey.value = key;           // 设置当前编辑字段
  editValue.value = profile[key];       // 将当前值填入输入框
  errorMessage.value = '';              // 清空错误信息
  isEditing.value = true;               // 显示弹窗
  
  // 100ms后自动聚焦输入框（等待DOM渲染完成）
  setTimeout(() => editInput.value?.focus(), 100);
};

/**
 * 保存编辑内容
 * 验证输入内容并更新数据
 */
const handleSave = async () => {
  // 验证：不能为空
  if (!editValue.value) { 
    errorMessage.value = "EMPTY_FIELD"; 
    return; 
  }
  
  // 防止重复提交
  if (isLoading.value) return;
  
  try {
    isLoading.value = true;
    errorMessage.value = '';
    
    // 构建请求参数
    const updateParam = {};
    updateParam[currentEditKey.value] = editValue.value;
    
    // 调用后端API
    const token = localStorage.getItem("token");
    const response = await axios.post('/api/update-user', updateParam, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (response.data.code === 200 && response.data.data) {
      // 更新本地数据
      profile[currentEditKey.value] = editValue.value;
      displayInfo[currentEditKey.value].visibleValue = editValue.value;
      
      // 更新localStorage中的用户信息
      if (currentEditKey.value === 'username' || currentEditKey.value === 'email') {
        const currentUserStr = localStorage.getItem("currentUser");
        if (currentUserStr) {
          const currentUser = JSON.parse(currentUserStr);
          currentUser[currentEditKey.value] = editValue.value;
          localStorage.setItem("currentUser", JSON.stringify(currentUser));
        }
      }
      
      // 关闭弹窗
      isEditing.value = false;
    } else {
      errorMessage.value = response.data.message || "UPDATE_FAILED";
    }
  } catch (error) {
    console.error('更新用户信息失败：', error);
    errorMessage.value = "NETWORK_ERROR";
  } finally {
    isLoading.value = false;
  }
};

/**
 * 返回主页
 */
const goHome = () => {
  const token = localStorage.getItem('token');
  // 先停止所有打字机动画，避免内存泄漏
  stopAllTyping();
  
  if (token) {
    router.push('/main');
  } else {
    router.push('/login');
  }
};

/**
 * 停止所有打字机动画
 */
const stopAllTyping = () => {
  // 重置所有打字状态
  for (const key in displayInfo) {
    displayInfo[key].isTyping = false;
  }
};
</script>

<style scoped>
/**
 * 样式说明：
 * 本页面采用黑客终端风格设计，主要特点：
 * 1. 绿色主题色（#33ff33）
 * 2. 等宽字体（Courier New）
 * 3. 3D透视变形效果
 * 4. 扫描线动画
 * 5. 打字机光标效果
 */

/* 主容器：全屏布局，设置背景图片和字体 */
.profile-container {
  width: 100vw; height: 100vh;
  background: #000 url('@/assets/background2.png') no-repeat right center;
  background-size: cover;
  overflow: hidden;
  display: flex; align-items: center;
  color: #33ff33;  /* 经典终端绿色 */
  font-family: 'Courier New', monospace;  /* 等宽字体 */
}

/* 背景遮罩层：半透明黑色叠加，增强文字可读性 */
.background-overlay {
  position: absolute; inset: 0;
  /* background: rgba(0, 15, 0, 0.5); */
  /* background: rgba(0, 0, 0, 0.9); */
}

/**
 * ============================================
 * 3D透视变形方案核心实现
 * ============================================
 * 
 * 变形原理：
 * 1. 在父容器开启3D透视（perspective），创建3D空间
 * 2. 在子元素应用3D变换（transform），实现倾斜效果
 * 3. 使用transform-origin控制变换的基准点
 */

/* 1. 透视舞台：开启3D空间，设置透视强度 */
.perspective-stage {
  perspective: 1200px;  /* 透视强度：值越小透视效果越明显 */
  width: 50%;
  padding-left: 8%;
  z-index: 2;  /* 确保在背景之上 */
}

/* 2. 变形面板：应用3D变换实现倾斜效果 */
.info-panel-deformed {
  position: relative;
  background: rgba(49, 111, 49, 0.4);  
  padding: 40px;
  width: 800px;
  /* border-left: 5px solid #33ff33;   左侧绿色边框 */
  transform: rotateY(25deg) rotateX(5deg);
  transform-origin: left center;
  
  /* 毛玻璃效果： backdrop-filter需要浏览器支持 */
  backdrop-filter: blur(4px);
  
  /* 左侧绿色阴影，增强立体感 */
  box-shadow: -20px 0 50px rgba(0, 255, 0, 0.2);
}

/* 内容包装器：承载实际内容，可独立设置样式 */
.content-wrapper {
  /* 
   * 注意：如果希望文字不受3D变形影响，可以在这里添加反向变换：
   * transform: rotateY(-25deg) rotateX(-5deg);
   * 但这样会失去整体的透视感，建议保持现状
   */
}

/* 终端风格标题 */
.terminal-title {
  font-size: 2.5rem;
  margin-bottom: 2rem;
  text-transform: uppercase;  /* 大写字母 */
  letter-spacing: 4px;        /* 字母间距 */
}

/* 信息项：可点击的资料行 */
.info-item {
  margin: 1.2rem 0;
  padding: 8px;
  cursor: pointer;
  border-bottom: 1px solid transparent;
  transition: all 0.3s;
  max-width: 100%; /* 限制不超过父容器 */
  overflow: hidden; /* 隐藏溢出内容 */
  word-wrap: break-word; /* 长单词换行 */
  word-break: break-all; /* 强制换行 */
}

/* 悬停效果：背景高亮+左侧缩进 */
.info-item:hover {
  background: rgba(51, 255, 51, 0.15);
  border-bottom: 1px solid #33ff33;
  padding-left: 15px;
}

/* 标签样式：半透明绿色 */
.label { 
  color: rgba(51, 255, 51, 0.5); 
  margin-right: 1rem;
  white-space: nowrap; /* 标签不换行 */
  flex-shrink: 0; /* 标签不收缩 */
}

/* 值样式：限制长度并换行 */
.value {
  word-wrap: break-word;
  word-break: break-all;
  overflow-wrap: break-word;
  max-width: calc(100% - 120px); /* 减去标签宽度 */
  display: inline-block;
  vertical-align: top;
}

/**
 * 扫描线效果
 * 模拟老式CRT显示器的扫描线动画
 * 从顶部移动到底部，循环播放
 */
.scanner-line {
  position: absolute; top: 0; left: 0; width: 100%; height: 2px;
  background: rgba(51, 255, 51, 0.4);
  box-shadow: 0 0 15px #33ff33;  /* 发光效果 */
  animation: scan 4s linear infinite;  /* 4秒循环一次 */
}

/* 扫描线动画关键帧 */
@keyframes scan {
  0% { top: 0; }
  100% { top: 100%; }
}

/**
 * 光标闪烁动画
 * 通过改变透明度实现闪烁效果
 */
.cursor { animation: blink 1s infinite; }
.cursor-sm { animation: blink 0.8s infinite; }

@keyframes blink { 
  0%, 100% { opacity: 1; } 
  50% { opacity: 0; } 
}

@keyframes scan-image {
  0% { top: 0; }
  100% { top: 100%; }
}

/* 图片标签 */
.image-label {
  margin-top: 10px;
  color: rgba(51, 255, 51, 0.6);
  font-size: 0.8rem;
  letter-spacing: 3px;
  text-align: center;
  text-transform: uppercase;
}

/**
 * ============================================
 * 弹窗样式（平面，不应用3D效果）
 * ============================================
 * 弹窗需要保持平面，确保用户输入体验
 */

/* 弹窗遮罩层：全屏半透明黑色 */
.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex; justify-content: center; align-items: center;
  z-index: 100;  /* 确保在最上层 */
}

/* 终端风格弹窗 */
.terminal-modal {
  width: 420px; 
  border: 1px solid #33ff33;
  background: #000; 
  padding: 25px;
  box-shadow: 0 0 40px rgba(0, 255, 0, 0.3);
}

/* 终端风格输入框 */
.terminal-input {
  width: 100%; 
  background: #001100;  /* 深绿色背景 */
  border: 1px solid #33ff33;
  color: #33ff33; 
  padding: 12px; 
  font-family: inherit;
  font-size: 1.1rem; 
  outline: none; 
  margin-top: 15px;
}

/* 按钮样式 */
.btn {
  background: none; 
  border: 1px solid #33ff33; 
  color: #33ff33;
  padding: 10px 20px; 
  cursor: pointer; 
  font-family: inherit;
  margin-right: 10px; 
  margin-top: 20px;
}

/* 按钮悬停效果：反色显示 */
.btn:hover:not(:disabled) { 
  background: #33ff33; 
  color: #000; 
}

/* 禁用状态的按钮 */
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  border-color: rgba(51, 255, 51, 0.5);
}

/* 淡入淡出过渡动画 */
.fade-enter-active, .fade-leave-active { 
  transition: opacity 0.4s; 
}
.fade-enter-from, .fade-leave-to { 
  opacity: 0; 
}

/* 返回主页按钮：左下角固定位置 */
.back-home-btn {
  position: fixed;
  bottom: 30px;
  left: 30px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border: 1px solid #33ff33;
  background: rgba(0, 17, 0, 0.8);
  color: #33ff33;
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 10;
}

.back-home-btn:hover {
  background: #33ff33;
  color: #000;
  box-shadow: 0 0 20px rgba(51, 255, 51, 0.5);
}

.btn-icon {
  font-size: 1.1rem;
  letter-spacing: -2px;
}

.btn-text {
  text-transform: uppercase;
  letter-spacing: 2px;
}
</style>
