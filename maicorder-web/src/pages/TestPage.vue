<template>
  <div class="badge-page">
    <h1 class="page-title">徽章生成器</h1>
    
    <!-- 控制面板 -->
    <div class="control-panel">
      <!-- 形状选择 -->
      <div class="control-group">
        <label>徽章形状：</label>
        <div class="shape-selector">
          <button 
            v-for="shape in shapes" 
            :key="shape.value"
            :class="['shape-btn', { active: selectedShape === shape.value }]"
            @click="selectedShape = shape.value"
          >
            {{ shape.label }}
          </button>
        </div>
      </div>
      
      <!-- 图片选择 -->
      <div class="control-group">
        <label>图片来源：</label>
        <div class="image-source">
          <button 
            :class="['source-btn', { active: imageSource === 'default' }]"
            @click="useDefaultImage"
          >
            默认曲绘
          </button>
          <button 
            :class="['source-btn', { active: imageSource === 'custom' }]"
            @click="imageSource = 'custom'"
          >
            自定义图片
          </button>
        </div>
      </div>
      
      <!-- 自定义图片上传 -->
      <div v-if="imageSource === 'custom'" class="control-group">
        <label>上传图片：</label>
        <input 
          type="file" 
          accept="image/*" 
          @change="handleImageUpload"
          class="file-input"
        />
      </div>
      
      <!-- 曲绘ID输入 -->
      <div v-if="imageSource === 'default'" class="control-group">
        <label>曲绘ID：</label>
        <input 
          type="text" 
          v-model="songId" 
          placeholder="输入曲绘ID"
          @keyup.enter="loadDefaultImage"
          class="text-input"
        />
        <button @click="loadDefaultImage" class="load-btn">加载</button>
      </div>
    </div>
    
    <!-- 预览区域 -->
    <div class="preview-area">
      <div class="badge-container">
        <div 
          class="badge"
          :class="`shape-${selectedShape}`"
        >
          <!-- 徽章图片 -->
          <div class="badge-image">
            <img 
              :src="currentImage"
              :alt="'徽章图片'"
              @error="handleImageError"
            />
          </div>
          <!-- 反光效果 -->
          <div class="badge-reflection"></div>
        </div>
      </div>
    </div>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <button @click="downloadBadge" class="action-btn download">
        下载徽章
      </button>
      <button @click="resetBadge" class="action-btn reset">
        重置
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

// 徽章形状选项
const shapes = [
  { label: '圆形', value: 'circle' },
  { label: '正方形', value: 'square' },
  { label: '长方形', value: 'rectangle' }
];

// 状态变量
const selectedShape = ref('circle');
const imageSource = ref('default');
const songId = ref('1001'); // 默认曲绘ID
const customImage = ref(null);
const currentImage = ref('');

// 基础URL
const BASE_URL = 'https://assets2.lxns.net/maimai';

// 计算当前图片URL
const getCurrentImage = computed(() => {
  if (imageSource.value === 'custom' && customImage.value) {
    return customImage.value;
  } else {
    return `${BASE_URL}/jacket/${songId.value}.png`;
  }
});

// 生命周期
onMounted(() => {
  loadDefaultImage();
});

// 加载默认曲绘
const loadDefaultImage = () => {
  currentImage.value = `${BASE_URL}/jacket/${songId.value}.png`;
};

// 使用默认图片
const useDefaultImage = () => {
  imageSource.value = 'default';
  loadDefaultImage();
};

// 处理自定义图片上传
const handleImageUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      customImage.value = e.target.result;
      currentImage.value = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

// 处理图片加载错误
const handleImageError = () => {
  // 加载失败时使用默认图片
  currentImage.value = `${BASE_URL}/jacket/1001.png`;
};

// 下载徽章
const downloadBadge = () => {
  if (!currentImage.value) return;
  
  const link = document.createElement('a');
  link.href = currentImage.value;
  link.download = `badge_${Date.now()}.png`;
  link.click();
};

// 重置
const resetBadge = () => {
  selectedShape.value = 'circle';
  imageSource.value = 'default';
  songId.value = '1001';
  customImage.value = null;
  loadDefaultImage();
};
</script>

<style scoped>
.badge-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: #ffffff;
  padding: 40px 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.page-title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 40px;
  color: #4cc9f0;
  text-shadow: 0 0 20px rgba(76, 201, 240, 0.5);
}

.control-panel {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 40px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.control-group {
  margin-bottom: 25px;
}

.control-group label {
  display: block;
  margin-bottom: 10px;
  font-weight: 600;
  color: #a8dadc;
}

.shape-selector,
.image-source {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.shape-btn,
.source-btn {
  padding: 10px 20px;
  border: 2px solid #4cc9f0;
  background: transparent;
  color: #4cc9f0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
}

.shape-btn:hover,
.source-btn:hover {
  background: rgba(76, 201, 240, 0.2);
  transform: translateY(-2px);
}

.shape-btn.active,
.source-btn.active {
  background: #4cc9f0;
  color: #1a1a2e;
  box-shadow: 0 0 20px rgba(76, 201, 240, 0.5);
}

.file-input {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  padding: 10px;
  color: #ffffff;
  width: 100%;
  cursor: pointer;
}

.file-input::-webkit-file-upload-button {
  background: #4cc9f0;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  color: #1a1a2e;
  font-weight: 600;
  cursor: pointer;
}

.text-input {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  padding: 10px 15px;
  color: #ffffff;
  width: 200px;
  margin-right: 10px;
}

.text-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.load-btn {
  padding: 10px 20px;
  background: #4cc9f0;
  border: none;
  border-radius: 6px;
  color: #1a1a2e;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.load-btn:hover {
  background: #4361ee;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(67, 97, 238, 0.4);
}

.preview-area {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 60px 0;
  min-height: 500px;
}

.badge-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 20px;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.badge {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  transition: all 0.3s ease;
}

.badge:hover {
  transform: scale(1.05);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.6);
}

/* 徽章形状 */
.badge.shape-circle {
  width: 300px;
  height: 300px;
  border-radius: 50%;
}

.badge.shape-square {
  width: 300px;
  height: 300px;
  border-radius: 12px;
}

.badge.shape-rectangle {
  width: 400px;
  height: 250px;
  border-radius: 12px;
}

.badge-image {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.badge-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.badge:hover .badge-image img {
  transform: scale(1.1);
}

/* 反光效果 */
.badge-reflection {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    to bottom right,
    rgba(255, 255, 255, 0.3) 0%,
    rgba(255, 255, 255, 0) 50%
  );
  transform: rotate(45deg);
  animation: reflection 3s infinite;
  pointer-events: none;
}

@keyframes reflection {
  0% {
    transform: translateX(-100%) translateY(-100%) rotate(45deg);
    opacity: 0.3;
  }
  100% {
    transform: translateX(100%) translateY(100%) rotate(45deg);
    opacity: 0;
  }
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
}

.action-btn {
  padding: 15px 30px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1rem;
}

.action-btn.download {
  background: linear-gradient(135deg, #4cc9f0, #4361ee);
  color: #1a1a2e;
  box-shadow: 0 4px 15px rgba(76, 201, 240, 0.4);
}

.action-btn.download:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(76, 201, 240, 0.6);
}

.action-btn.reset {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.action-btn.reset:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .badge-page {
    padding: 20px 10px;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .control-panel {
    padding: 20px;
  }
  
  .shape-selector,
  .image-source {
    flex-direction: column;
  }
  
  .badge.shape-circle,
  .badge.shape-square {
    width: 250px;
    height: 250px;
  }
  
  .badge.shape-rectangle {
    width: 300px;
    height: 200px;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .action-btn {
    width: 200px;
  }
}
</style>
