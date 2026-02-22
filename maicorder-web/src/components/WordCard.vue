<script setup>
import { computed } from 'vue';

// 定义 Props
const props = defineProps({
  title: { type: String, default: '' },
  subTitle: { type: String, default: '' },
  image: { type: String, default: '' },
  variant: {
    type: String,
    default: 'filled', 
    validator: (value) => ['elevated', 'filled', 'outlined'].includes(value)
  },
  clickable: { type: Boolean, default: false }
});

// 计算类名
const cardClasses = computed(() => {
  return [
    'md3-card',
    `md3-card--${props.variant}`,
    { 'md3-card--clickable': props.clickable }
  ];
});
</script>

<template>
  <div :class="cardClasses">
    <!-- 状态层 -->
    <div class="md3-card__state-layer"></div>

    <!-- 媒体区域 -->
    <div v-if="image" class="md3-card__media">
      <img :src="image" :alt="title" />
    </div>

    <!-- 头部区域：日期（大字左） + 地点（小字在日期右侧） -->
    <div class="md3-card__header" v-if="title || subTitle">
      <div class="header-row">
        <!-- 左侧大字日期 -->
        <h2 class="md3-card__title">{{ title }}</h2>
        <!-- 右侧小字地点（无logo，紧跟日期右侧） -->
        <h3 class="md3-card__subtitle" v-if="subTitle">{{ subTitle }}</h3>
      </div>
    </div>

    <!-- 内容插槽 -->
    <div class="md3-card__content">
      <slot></slot>
    </div>

    <!-- 底部操作栏 -->
    <div class="md3-card__actions" v-if="$slots.actions">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<style scoped>
/* 重新定义主题变量 */
.md3-card {
  --md-sys-color-primary: #000000; /* 改为黑色 */
  --md-sys-color-on-primary: #ffffff;
  --md-sys-color-primary-container: #ffffff;
  --md-sys-color-on-primary-container: #000000;
  
  --md-sys-color-surface: rgba(255, 255, 255, 0.6); /* 白色80%透明度 */
  --md-sys-color-surface-container-highest: rgba(255, 255, 255, 0.7);
  --md-sys-color-on-surface: #000000; /* 黑色文字 */
  --md-sys-color-on-surface-variant: #333333; /* 深灰色副标题 */
  --md-sys-color-outline: #000000; /* 黑色描边 */
  
  /* 移除阴影 */
  --md-sys-elevation-1: none;
  --md-sys-elevation-2: none;
  
  /* 无圆角 */
  --md-sys-shape-corner: 0px;
}

.md3-card {
  position: relative;
  display: flex;
  flex-direction: column;
  border-radius: var(--md-sys-shape-corner); /* 无圆角 */
  overflow: hidden;
  text-align: left;
  transition: box-shadow 0.2s cubic-bezier(0.2, 0, 0, 1), background-color 0.2s linear;
  font-family: 'Roboto', sans-serif;
  isolation: isolate;
  margin-bottom: 16px; /* 增加卡片间距 */
  /* 白色80%透明度背景 */
  background-color: var(--md-sys-color-surface);
  /* 加粗的黑色描边 - 从1px改为2px */
  border: 2px solid var(--md-sys-color-outline);
  /* 移除阴影 */
  box-shadow: var(--md-sys-elevation-1);
}

.md3-card__state-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--md-sys-color-outline); /* 使用黑色作为状态层颜色 */
  opacity: 0;
  z-index: -1;
  transition: opacity 0.2s ease;
  pointer-events: none;
}

.md3-card--clickable {
  cursor: pointer;
}

.md3-card--clickable:hover .md3-card__state-layer {
  opacity: 0.08;
}

.md3-card--clickable:active .md3-card__state-layer {
  opacity: 0.12;
}

/* 修改 elevated 变体 - 使用白色透明背景和加粗黑色描边 */
.md3-card--elevated {
  background-color: var(--md-sys-color-surface);
  color: var(--md-sys-color-on-surface);
  border: 2px solid var(--md-sys-color-outline); /* 加粗描边 */
  box-shadow: var(--md-sys-elevation-1);
}
.md3-card--elevated.md3-card--clickable:hover {
  box-shadow: var(--md-sys-elevation-2);
  background-color: rgba(255, 255, 255, 0.9); /* 悬停时稍微增加不透明度 */
}

/* 修改 filled 变体 - 使用稍深的白色透明背景和加粗黑色描边 */
.md3-card--filled {
  background-color: var(--md-sys-color-surface-container-highest);
  color: var(--md-sys-color-on-surface);
  border: 2px solid var(--md-sys-color-outline); /* 加粗描边 */
}

/* 修改 outlined 变体 - 保持加粗黑色描边 */
.md3-card--outlined {
  background-color: var(--md-sys-color-surface);
  border: 2px solid var(--md-sys-color-outline); /* 加粗描边 */
  color: var(--md-sys-color-on-surface);
}

/* ========== 头部布局 ========== */
.md3-card__header {
  padding: 16px 16px 0 16px;
}
/* 日期+地点 左对齐，地点紧跟日期右侧 */
.header-row {
  display: flex;
  align-items: center; /* 垂直居中 */
  gap: 12px; /* 日期和地点的间距（可调整） */
  width: 100%;
}
.md3-card__title {
  margin: 0;
  font-size: 22px;
  line-height: 28px;
  font-weight: 600; /* 日期加粗更醒目 */
  color: var(--md-sys-color-on-surface);
  white-space: nowrap; /* 防止日期换行 */
}
.md3-card__subtitle {
  margin: 0;
  font-size: 14px;
  line-height: 20px;
  font-weight: 400;
  color: var(--md-sys-color-on-surface-variant);
  white-space: nowrap; /* 防止地点换行 */
}

/* 原有样式保留 */
.md3-card__media img {
  width: 100%;
  height: auto;
  display: block;
  object-fit: cover;
}

.md3-card__content {
  padding: 16px;
  font-size: 14px;
  line-height: 20px;
  letter-spacing: 0.25px;
  color: var(--md-sys-color-on-surface-variant);
  width: 100%;
  box-sizing: border-box;
  background: transparent; /* 确保内容区域透明 */
}

.md3-card__actions {
  padding: 8px 16px 16px;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  background: transparent; /* 确保操作区域透明 */
}
</style>