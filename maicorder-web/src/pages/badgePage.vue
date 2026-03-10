<template>
  <div class="app-container">
    <nav class="navbar">
      <div class="brand">
        <span class="logo-icon">◈</span>
        <span class="logo-text">BADGE<span>LAB</span></span>
      </div>
      <div class="spacer"></div>
      <div class="version">v2.0 Minimalist</div>
    </nav>

    <main class="workspace">
      <aside class="config-section">
        <div class="card">
          <section class="control-group">
            <h3 class="group-title">素材配置</h3>
            
            <div class="field">
              <label>曲目 ID (Maimai)</label>
              <div class="input-inner">
                <input 
                  v-model="songId" 
                  type="text" 
                  placeholder="例如: 11253" 
                  @input="handleIdInput" 
                />
              </div>
            </div>

            <div class="field">
              <label>自定义图片</label>
              <button class="upload-trigger" @click="triggerFileSelect">
                <span v-if="!customImageUrl">选取本地文件</span>
                <span v-else class="success">已成功加载图片</span>
              </button>
              <input 
                type="file" 
                ref="fileInput" 
                accept="image/*" 
                @change="handleFileUpload" 
                class="hidden-input" 
              />
            </div>
          </section>

          <section class="control-group">
            <h3 class="group-title">物理规格</h3>
            <div class="shape-grid">
              <button 
                :class="['shape-btn', { active: shape === 'circle' }]" 
                @click="shape = 'circle'"
              >圆形</button>
              <button 
                :class="['shape-btn', { active: shape === 'square' }]" 
                @click="shape = 'square'"
              >正方</button>
              <button 
                :class="['shape-btn', { active: shape === 'rect' }]" 
                @click="shape = 'rect'"
              >长方</button>
            </div>
          </section>

          <footer class="card-footer">
            提示：移动鼠标观察徽章表面反光
          </footer>
        </div>
      </aside>

      <section class="preview-section">
        <div 
          class="badge-wrapper" 
          :style="tiltStyle"
          @mousemove="handleMouseMove" 
          @mouseleave="resetTilt"
        >
          <div :class="['badge-entity', shape]">
            <img :src="imageUrl" class="jacket-image" alt="Badge Content" />
            
            <div class="gloss-reflection"></div>
            
            <div class="ambient-finish"></div>
          </div>
          
          <div class="dynamic-shadow" :style="shadowStyle"></div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

// --- 状态定义 ---
const songId = ref('1296');
const customImageUrl = ref(null);
const shape = ref('circle');
const fileInput = ref(null);

// --- 3D 动效逻辑 ---
const rotateX = ref(0);
const rotateY = ref(0);
const lightX = ref(50); // 光源位置百分比

const tiltStyle = computed(() => ({
  transform: `rotateX(${rotateX.value}deg) rotateY(${rotateY.value}deg)`
}));

const shadowStyle = computed(() => ({
  transform: `translateZ(-50px) translateX(${-rotateY.value * 2}px) translateY(${rotateX.value * 2}px)`,
  opacity: 0.15 + Math.abs(rotateX.value) / 100
}));

const handleMouseMove = (e) => {
  const rect = e.currentTarget.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  
  const centerX = rect.width / 2;
  const centerY = rect.height / 2;
  
  // 计算旋转角度 (范围约 -15deg 到 15deg)
  rotateX.value = -(y - centerY) / 10;
  rotateY.value = (x - centerX) / 10;
  
  // 计算光源移动
  lightX.value = (x / rect.width) * 100;
};

const resetTilt = () => {
  rotateX.value = 0;
  rotateY.value = 0;
  lightX.value = 50;
};

// --- 图片处理逻辑 ---
const BASE_URL = 'https://assets2.lxns.net/maimai/jacket';

const imageUrl = computed(() => {
  if (customImageUrl.value) return customImageUrl.value;
  return `${BASE_URL}/${songId.value}.png`;
});

const triggerFileSelect = () => {
  fileInput.value.click();
};

const handleFileUpload = (e) => {
  const file = e.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (ev) => {
      customImageUrl.value = ev.target.result;
      songId.value = ''; // 清空 ID
    };
    reader.readAsDataURL(file);
  }
};

const handleIdInput = () => {
  if (songId.value) customImageUrl.value = null;
};
</script>

<style scoped>
/* 基础布局 */
.app-container {
  min-height: 100vh;
  background-color: #f4f4f7;
  color: #1a1a1a;
  font-family: 'Inter', -apple-system, sans-serif;
  display: flex;
  flex-direction: column;
}

/* 极简导航 */
.navbar {
  height: 70px;
  padding: 0 40px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e5e5e5;
  background: #fff;
}
.brand { display: flex; align-items: center; gap: 10px; }
.logo-icon { color: #007AFF; font-size: 1.2rem; }
.logo-text { font-weight: 800; letter-spacing: -0.5px; font-size: 1.1rem; }
.logo-text span { color: #888; font-weight: 300; }
.version { font-size: 0.75rem; color: #999; text-transform: uppercase; letter-spacing: 1px; }

/* 工作区布局 */
.workspace {
  flex: 1;
  display: flex;
  padding: 40px;
  gap: 40px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

/* 控制面板卡片 */
.config-section { width: 340px; }
.card {
  background: #fff;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.03);
  border: 1px solid rgba(0,0,0,0.05);
}
.control-group { margin-bottom: 30px; }
.group-title {
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  color: #999;
  margin-bottom: 20px;
}

/* 表单样式 */
.field { margin-bottom: 20px; }
.field label { display: block; font-size: 0.85rem; margin-bottom: 8px; font-weight: 500; }
.input-inner input {
  width: 100%;
  padding: 12px 16px;
  border-radius: 10px;
  border: 1px solid #eee;
  background: #f9f9f9;
  outline: none;
  transition: all 0.2s;
}
.input-inner input:focus { border-color: #007AFF; background: #fff; }

.upload-trigger {
  width: 100%;
  padding: 12px;
  border-radius: 10px;
  border: 2px dashed #eee;
  background: transparent;
  cursor: pointer;
  font-size: 0.85rem;
  color: #666;
  transition: all 0.2s;
}
.upload-trigger:hover { border-color: #007AFF; color: #007AFF; background: rgba(0,122,255,0.02); }
.success { color: #27ae60; font-weight: 600; }
.hidden-input { display: none; }

/* 形状切换 */
.shape-grid { display: flex; gap: 8px; }
.shape-btn {
  flex: 1;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #eee;
  background: #fff;
  cursor: pointer;
  font-size: 0.8rem;
  transition: all 0.2s;
}
.shape-btn.active { background: #1a1a1a; color: #fff; border-color: #1a1a1a; }

.card-footer { font-size: 0.7rem; color: #bbb; text-align: center; margin-top: 20px; }

/* 展示区域 */
.preview-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fff;
  border-radius: 24px;
  perspective: 1000px;
}

.badge-wrapper {
  position: relative;
  transform-style: preserve-3d;
  transition: transform 0.1s ease-out;
}

/* 徽章实体 */
.badge-entity {
  position: relative;
  z-index: 2;
  overflow: hidden;
  box-shadow: 0 0 1px rgba(0,0,0,0.2);
  background: #000;
}

/* 尺寸与形状 */
.badge-entity.circle { width: 320px; height: 320px; border-radius: 50%; }
.badge-entity.square { width: 320px; height: 320px; border-radius: 20px; }
.badge-entity.rect { width: 400px; height: 260px; border-radius: 12px; }

.jacket-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 核心质感：高光 */
.gloss-reflection {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  /* 混合渐变模拟弧形反光 */
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.5) 0%,
    rgba(255, 255, 255, 0.1) 30%,
    rgba(255, 255, 255, 0) 60%,
    rgba(0, 0, 0, 0.05) 100%
  );
  pointer-events: none;
}

/* 核心质感：边缘打磨 */
.ambient-finish {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  /* 内部阴影模拟边缘凸起 */
  box-shadow: 
    inset 0 4px 12px rgba(255,255,255,0.2),
    inset 0 -4px 12px rgba(0,0,0,0.1);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: inherit;
  pointer-events: none;
}

/* 动态影子 */
.dynamic-shadow {
  position: absolute;
  top: 20px; left: 10px; right: 10px; bottom: -20px;
  background: rgba(0,0,0,0.2);
  filter: blur(40px);
  z-index: 1;
  border-radius: inherit;
  pointer-events: none;
  transition: transform 0.1s ease-out;
}

/* 响应式 */
@media (max-width: 900px) {
  .workspace { flex-direction: column; padding: 20px; }
  .config-section { width: 100%; }
  .badge-entity.circle, .badge-entity.square { width: 260px; height: 260px; }
  .badge-entity.rect { width: 300px; height: 200px; }
}
</style>