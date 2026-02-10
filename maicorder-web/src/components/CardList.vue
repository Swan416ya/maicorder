<!-- components/CardList.vue -->
<template>
  <div class="card-list-container">
    <!-- 1. 加载状态 -->
    <div v-if="loading" class="list-state">
      <div class="shimmer">正在获取数据...</div>
    </div>

    <!-- 2. 空状态 -->
    <div v-else-if="!items || items.length === 0" class="list-state">
      <div class="empty-notice">
        <slot name="empty">暂无记录</slot>
      </div>
    </div>

    <!-- 3. 卡片列表 -->
    <div v-else class="list-wrapper">
      <div 
        v-for="(item, index) in items" 
        :key="item.id || index"
        class="list-item-anim"
        :style="{ 'animation-delay': `${index * 0.05}s` }"
      >
        <slot :item="item">
          <!-- 默认卡片样式（如果外部没有传插槽内容） -->
          <div class="default-card">{{ item.title || '未命名卡片' }}</div>
        </slot>
      </div>
      
      <!-- 底部留白，防止被导航栏遮挡 -->
      <div class="list-footer-spacer"></div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  items: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
});
</script>

<style scoped>
.card-list-container {
  width: 100%;
}

.list-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px; /* M3 标准卡片间距 */
  padding: 8px 0;
}

.list-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  color: #6750a4;
  font-weight: 500;
}

.empty-notice {
  padding: 40px;
  border: 1px dashed rgba(103, 80, 164, 0.3);
  border-radius: 16px;
  text-align: center;
  width: 100%;
}

.list-footer-spacer {
  height: 80px;
}

/* 入场动画：卡片依次浮现 */
.list-item-anim {
  opacity: 0;
  transform: translateY(20px);
  animation: slideUpIn 0.5s cubic-bezier(0.22, 1, 0.36, 1) forwards;
}

@keyframes slideUpIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.shimmer {
  opacity: 0.6;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  50% { opacity: 0.3; }
}
</style>