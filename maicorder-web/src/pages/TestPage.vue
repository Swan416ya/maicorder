<template>
  <div class="app-container">
    <nav class="navbar">
      <div class="brand">
        <span class="logo-icon">◈</span>
        <span class="logo-text">BADGE<span>LAB</span></span>
      </div>
      <div class="spacer"></div>
      <div class="version">v2.1 Arched Text</div>
    </nav>

    <main class="workspace">
      <aside class="config-section">
        <div class="card">
          <section class="control-group">
            <h3 class="group-title">素材配置</h3>
            
            <div class="field">
              <label>曲目 ID (Maimai)</label>
              <div class="input-inner">
                <input v-model="songId" type="text" placeholder="例如: 11253" @input="handleIdInput" />
              </div>
            </div>

            <div class="field">
              <label>自定义图片</label>
              <button class="upload-trigger" @click="triggerFileSelect">
                <span v-if="!customImageUrl">选取本地文件</span>
                <span v-else class="success">已成功加载图片</span>
              </button>
              <input type="file" ref="fileInput" accept="image/*" @change="handleFileUpload" class="hidden-input" />
            </div>
          </section>

          <section class="control-group">
            <h3 class="group-title">物理规格</h3>
            <div class="shape-grid">
              <button :class="['shape-btn', { active: shape === 'circle' }]" @click="shape = 'circle'">圆形</button>
              <button :class="['shape-btn', { active: shape === 'square' }]" @click="shape = 'square'">正方</button>
              <button :class="['shape-btn', { active: shape === 'rect' }]" @click="shape = 'rect'">长方</button>
            </div>
          </section>

          <footer class="card-footer">
            提示：左下角大字已动态贴合徽章圆弧
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
            
            <svg class="arched-text-svg" viewBox="0 0 100 100" preserveAspectRatio="none">
              <defs>
                <path 
                  id="leftBottomArc" 
                  d="M 10,65 A 40,40 0 0 0 35,90" 
                  fill="none"
                />
              </defs>
              <text class="warped-text">
                <textPath xlink:href="#leftBottomArc" startOffset="0%">
                  SIMPLE
                </textPath>
              </text>
            </svg>

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
const songId = ref('11253');
const customImageUrl = ref(null);
const shape = ref('circle');
const fileInput = ref(null);

// --- 3D 动效逻辑保持不变 ---
const rotateX = ref(0);
const rotateY = ref(0);
const tiltStyle = computed(() => ({ transform: `rotateX(${rotateX.value}deg) rotateY(${rotateY.value}deg)` }));
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
  rotateX.value = -(y - centerY) / 10;
  rotateY.value = (x - centerX) / 10;
};
const resetTilt = () => { rotateX.value = 0; rotateY.value = 0; };

// --- 图片处理逻辑保持不变 ---
const BASE_URL = 'https://assets2.lxns.net/maimai/jacket';
const imageUrl = computed(() => customImageUrl.value || `${BASE_URL}/${songId.value}.png`);
const triggerFileSelect = () => fileInput.value.click();
const handleFileUpload = (e) => {
  const file = e.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (ev) => { customImageUrl.value = ev.target.result; songId.value = ''; };
    reader.readAsDataURL(file);
  }
};
const handleIdInput = () => { if (songId.value) customImageUrl.value = null; };
</script>

<style scoped>
/* 1. 基础布局保持不变 (省略部分以节省篇幅，保持与上一次回答一致) */
.app-container { min-height: 100vh; background-color: #f4f4f7; color: #1a1a1a; font-family: 'Inter', -apple-system, sans-serif; display: flex; flex-direction: column; }
.navbar { height: 70px; padding: 0 40px; display: flex; align-items: center; border-bottom: 1px solid #e5e5e5; background: #fff; z-index: 10; }
.brand { display: flex; align-items: center; gap: 10px; }
.logo-icon { color: #007AFF; font-size: 1.2rem; }
.logo-text { font-weight: 800; letter-spacing: -0.5px; font-size: 1.1rem; }
.logo-text span { color: #888; font-weight: 300; }
.version { font-size: 0.75rem; color: #999; text-transform: uppercase; letter-spacing: 1px; }

.workspace { flex: 1; display: flex; padding: 40px; gap: 40px; max-width: 1400px; margin: 0 auto; width: 100%; align-items: center; }

.config-section { width: 340px; }
.card { background: #fff; border-radius: 20px; padding: 30px; box-shadow: 0 10px 30px rgba(0,0,0,0.03); border: 1px solid rgba(0,0,0,0.05); }
.control-group { margin-bottom: 30px; }
.group-title { font-size: 0.7rem; text-transform: uppercase; letter-spacing: 1.5px; color: #999; margin-bottom: 20px; }
.field { margin-bottom: 20px; }
.field label { display: block; font-size: 0.85rem; margin-bottom: 8px; font-weight: 500; }
.input-inner input { width: 100%; padding: 12px 16px; border-radius: 10px; border: 1px solid #eee; background: #f9f9f9; outline: none; transition: all 0.2s; }
.input-inner input:focus { border-color: #007AFF; background: #fff; }
.upload-trigger { width: 100%; padding: 12px; border-radius: 10px; border: 2px dashed #eee; background: transparent; cursor: pointer; font-size: 0.85rem; color: #666; transition: all 0.2s; }
.upload-trigger:hover { border-color: #007AFF; color: #007AFF; background: rgba(0,122,255,0.02); }
.success { color: #27ae60; font-weight: 600; }
.hidden-input { display: none; }
.shape-grid { display: flex; gap: 8px; }
.shape-btn { flex: 1; padding: 10px; border-radius: 8px; border: 1px solid #eee; background: #fff; cursor: pointer; font-size: 0.8rem; transition: all 0.2s; }
.shape-btn.active { background: #1a1a1a; color: #fff; border-color: #1a1a1a; }
.card-footer { font-size: 0.7rem; color: #bbb; text-align: center; margin-top: 20px; }

.preview-section { flex: 1; display: flex; justify-content: center; align-items: center; background: #fff; border-radius: 24px; perspective: 1200px; min-height: 500px; }

.badge-wrapper { position: relative; transform-style: preserve-3d; transition: transform 0.1s ease-out; }

/* 徽章实体保持不变 */
.badge-entity { position: relative; z-index: 2; overflow: hidden; box-shadow: 0 0 1px rgba(0,0,0,0.2); background: #000; }
.badge-entity.circle { width: 340px; height: 340px; border-radius: 50%; }
.badge-entity.square { width: 340px; height: 340px; border-radius: 20px; }
.badge-entity.rect { width: 420px; height: 280px; border-radius: 14px; }

.jacket-image { width: 100%; height: 100%; object-fit: cover; display: block; }

/* =========================================
   2. [核心新样式]：圆弧变形大字
   ========================================= */
.arched-text-svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none; /* 不干扰鼠标对徽章的交互 */
  z-index: 3; /* 在图片之上，在反光层之下 */
  
  /* [变形效果的关键]：
     通过 skewY 和 scale 模拟文字随表面凸起产生的透视拉伸 */
  transform: scaleY(1.1) skewY(-2deg);
  transform-origin: bottom left; /* 以左下角为变形基点 */
  
  /* 增加一点模糊，使其看起来更像印在塑料膜上的胶印效果，而不是矢量的 */
  filter: blur(0.2px); 
}

.warped-text {
  /* 字体设置 */
  font-family: 'Inter', 'Futura', 'Helvetica Neue', sans-serif;
  font-weight: 900; /* 超粗 */
  font-size: 15px; /* 在 100x100 的 viewBox 中这已经非常大了 */
  text-transform: uppercase;
  letter-spacing: 1px; /* 字间距 */
  
  /* 颜色与混合模式 */
  fill: rgba(255, 255, 255, 0.85); /* 半透明白，模拟油墨感 */
  
  /* [融合效果的关键]：
     'overlay' (叠加) 或 'soft-light' (柔光) 
     让文字颜色与底下的图片色彩融合，看起来像是印在上面的 */
  mix-blend-mode: overlay; 
  
  /* 如果混合模式在某些浏览器无效，兜底方案 */
  -webkit-backface-visibility: hidden;
}

/* =========================================
   其他质感样式保持不变
   ========================================= */
.gloss-reflection { position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: linear-gradient( 135deg, rgba(255, 255, 255, 0.5) 0%, rgba(255, 255, 255, 0.1) 30%, rgba(255, 255, 255, 0) 60%, rgba(0, 0, 0, 0.05) 100% ); pointer-events: none; z-index: 4; }
.ambient-finish { position: absolute; top: 0; left: 0; right: 0; bottom: 0; box-shadow: inset 0 4px 12px rgba(255,255,255,0.2), inset 0 -4px 12px rgba(0,0,0,0.1); border: 1px solid rgba(255,255,255,0.1); border-radius: inherit; pointer-events: none; z-index: 5; }
.dynamic-shadow { position: absolute; top: 20px; left: 10px; right: 10px; bottom: -20px; background: rgba(0,0,0,0.2); filter: blur(40px); z-index: 1; border-radius: inherit; pointer-events: none; transition: transform 0.1s ease-out; }

@media (max-width: 900px) { .workspace { flex-direction: column; padding: 20px; } .config-section { width: 100%; } .badge-entity.circle, .badge-entity.square { width: 280px; height: 280px; } .badge-entity.rect { width: 320px; height: 220px; } .warped-text { font-size: 18px; } }
</style>