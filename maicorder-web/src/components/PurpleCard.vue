<script setup>
import { computed } from 'vue';

// 定义 Props
const props = defineProps({
  // 卡片标题
  title: {
    type: String,
    default: ''
  },
  // 副标题
  subTitle: {
    type: String,
    default: ''
  },
  // 封面图片 URL
  image: {
    type: String,
    default: ''
  },
  // MD3 变体: 'elevated' | 'filled' | 'outlined'
  variant: {
    type: String,
    default: 'filled', 
    validator: (value) => ['elevated', 'filled', 'outlined'].includes(value)
  },
  // 是否可点击（添加涟漪效果模拟）
  clickable: {
    type: Boolean,
    default: false
  }
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
    <!-- 状态层 (State Layer) 用于处理 hover/focus 遮罩 -->
    <div class="md3-card__state-layer"></div>

    <!-- 媒体区域 (图片) -->
    <div v-if="image" class="md3-card__media">
      <img :src="image" :alt="title" />
    </div>

    <!-- 头部区域 -->
    <div class="md3-card__header" v-if="title || subTitle">
      <h2 class="md3-card__title" v-if="title">{{ title }}</h2>
      <h3 class="md3-card__subtitle" v-if="subTitle">{{ subTitle }}</h3>
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
/* 
  MD3 紫色主题变量 (局部作用域) 
  在真实项目中，这些应该定义在全局 :root 中
*/
.md3-card {
  --md-sys-color-primary: #6750a4; /* MD3 标准紫色 */
  --md-sys-color-on-primary: #ffffff;
  --md-sys-color-primary-container: #eaddff;
  --md-sys-color-on-primary-container: #21005d;
  
  --md-sys-color-surface: #fffbfe;
  --md-sys-color-surface-container-highest: #e6e0e9; /* Filled 卡片背景 */
  --md-sys-color-on-surface: #1c1b1f;
  --md-sys-color-on-surface-variant: #49454f;
  --md-sys-color-outline: #79747e;
  
  --md-sys-elevation-1: 0px 1px 2px 0px rgba(0, 0, 0, 0.3), 0px 1px 3px 1px rgba(0, 0, 0, 0.15);
  --md-sys-elevation-2: 0px 1px 2px 0px rgba(0, 0, 0, 0.3), 0px 2px 6px 2px rgba(0, 0, 0, 0.15);
  
  --md-sys-shape-corner: 12px;
}

/* 基础卡片样式 */
.md3-card {
  position: relative;
  display: flex;
  flex-direction: column;
  border-radius: var(--md-sys-shape-corner);
  overflow: hidden; /* 确保图片和状态层不溢出圆角 */
  text-align: left;
  transition: box-shadow 0.2s cubic-bezier(0.2, 0, 0, 1), background-color 0.2s linear;
  font-family: 'Roboto', sans-serif;
  isolation: isolate; /* 创建新的堆叠上下文 */
}

/* 
  State Layer (交互层)
  MD3 不直接改变背景色，而是在上方叠加一个半透明层
*/
.md3-card__state-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--md-sys-color-on-surface);
  opacity: 0;
  z-index: -1; /* 位于内容之下 */
  transition: opacity 0.2s ease;
  pointer-events: none;
}

/* 交互效果 */
.md3-card--clickable {
  cursor: pointer;
}

.md3-card--clickable:hover .md3-card__state-layer {
  opacity: 0.08; /* Hover opacity */
}

.md3-card--clickable:active .md3-card__state-layer {
  opacity: 0.12; /* Press opacity */
}

/* ================== 变体样式 (Variants) ================== */

/* 1. Elevated (提升卡片): 低对比度背景 + 阴影 */
.md3-card--elevated {
  background-color: var(--md-sys-color-surface);
  color: var(--md-sys-color-on-surface);
  box-shadow: var(--md-sys-elevation-1);
}
.md3-card--elevated.md3-card--clickable:hover {
  box-shadow: var(--md-sys-elevation-2);
}

/* 2. Filled (填充卡片): 最高的色彩表现，无阴影，背景带色调 */
.md3-card--filled {
  background-color: var(--md-sys-color-surface-container-highest); /* 浅紫色背景 */
  color: var(--md-sys-color-on-surface);
  border: none;
}
/* Filled 卡片 Hover 时通常不加阴影，只加深背景（通过 State Layer 实现） */

/* 3. Outlined (轮廓卡片): 透明背景 + 边框 */
.md3-card--outlined {
  background-color: var(--md-sys-color-surface);
  border: 1px solid var(--md-sys-color-outline);
  color: var(--md-sys-color-on-surface);
}

/* ================== 内部布局 ================== */

.md3-card__media img {
  width: 100%;
  height: auto;
  display: block;
  object-fit: cover;
}

.md3-card__header {
  padding: 16px 16px 0 16px;
}

.md3-card__title {
  margin: 0;
  font-size: 22px;
  line-height: 28px;
  font-weight: 400;
  color: var(--md-sys-color-on-surface);
}

.md3-card__subtitle {
  margin: 4px 0 0 0;
  font-size: 14px;
  line-height: 20px;
  font-weight: 400;
  color: var(--md-sys-color-on-surface-variant);
}

.md3-card__content {
  padding: 16px;
  font-size: 14px;
  line-height: 20px;
  letter-spacing: 0.25px;
  color: var(--md-sys-color-on-surface-variant);
}

.md3-card__actions {
  padding: 8px 16px 16px; /* 右对齐按钮通常需要 padding */
  display: flex;
  gap: 8px;
  justify-content: flex-end; /* MD3 默认操作按钮右对齐 */
}
</style>