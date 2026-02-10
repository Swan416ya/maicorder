<script setup>
import { computed } from 'vue';

const props = defineProps({
  // 按钮文字
  label: {
    type: String,
    required: true
  },
  // 是否处于展开状态 (true: 文字+图标, false: 仅图标)
  extended: {
    type: Boolean,
    default: true
  },
  // 变体: 'primary' (深紫底白字) | 'surface' (浅紫底深紫字 - MD3 默认推荐)
  variant: {
    type: String,
    default: 'surface',
    validator: (v) => ['primary', 'surface'].includes(v)
  },
  // 是否固定在屏幕右下角
  fixed: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['click']);

const classes = computed(() => [
  'md3-fab',
  `md3-fab--${props.variant}`,
  { 'md3-fab--extended': props.extended },
  { 'md3-fab--fixed': props.fixed }
]);
</script>

<template>
  <button :class="classes" @click="emit('click')">
    <!-- 状态层 (State Layer) -->
    <div class="md3-fab__state-layer"></div>

    <!-- 图标插槽 -->
    <div class="md3-fab__icon">
      <slot name="icon">
        <!-- 默认加号图标 -->
        <svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 -960 960 960" width="24" fill="currentColor">
          <path d="M440-440H200v-80h240v-240h80v240h240v80H520v240h-80v-240Z"/>
        </svg>
      </slot>
    </div>

    <!-- 文本标签 (利用 CSS Grid/Flex 实现平滑收缩动画) -->
    <div class="md3-fab__label">
      <span>{{ label }}</span>
    </div>
  </button>
</template>

<style scoped>
/* 定义变量 (与 Card 组件一致的紫色系) */
.md3-fab {
  --md-fab-primary-bg: #6750a4;
  --md-fab-primary-color: #ffffff;
  
  --md-fab-surface-bg: #eaddff; /* Surface Container Highest */
  --md-fab-surface-color: #21005d; /* On Surface Variant */

  --md-elevation-3: 0px 4px 8px 3px rgba(0, 0, 0, 0.15), 0px 1px 3px 0px rgba(0, 0, 0, 0.3);
  --md-elevation-4: 0px 6px 10px 4px rgba(0, 0, 0, 0.15), 0px 2px 3px 0px rgba(0, 0, 0, 0.3);
}

.md3-fab {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  min-width: 56px;
  height: 56px;
  padding: 0;
  border: none;
  outline: none;
  border-radius: 16px; /* MD3 标准圆角 */
  cursor: pointer;
  font-family: 'Roboto', sans-serif;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.1px;
  transition: all 0.3s cubic-bezier(0.2, 0, 0, 1); /* MD3 标准缓动 */
  box-shadow: var(--md-elevation-3);
  overflow: hidden;
  z-index: 5;
}

/* ================== 变体颜色 ================== */

/* Surface Variant (浅色背景，通常用于次要或默认 FAB) */
.md3-fab--surface {
  background-color: var(--md-fab-surface-bg);
  color: var(--md-fab-surface-color);
}

/* Primary Variant (深色背景，高强调) */
.md3-fab--primary {
  background-color: var(--md-fab-primary-bg);
  color: var(--md-fab-primary-color);
}

/* ================== 布局与动画 ================== */

/* 1. 图标区域 */
.md3-fab__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px; /* 固定宽度确保收缩时居中 */
  height: 56px;
  flex-shrink: 0;
}

/* 2. 文本区域 */
.md3-fab__label {
  display: flex;
  align-items: center;
  white-space: nowrap;
  overflow: hidden;
  width: 0; /* 默认收缩状态宽度为 0 */
  opacity: 0;
  transition: width 0.3s cubic-bezier(0.2, 0, 0, 1), opacity 0.2s ease;
}

.md3-fab__label span {
  padding-right: 24px; /* 文本右侧 padding */
}

/* 3. 展开状态 (.md3-fab--extended) */
.md3-fab--extended {
  padding-right: 0; /* 这里的 padding 由 label 内部控制 */
  min-width: unset; /* 移除最小宽度限制，允许变宽 */
}

.md3-fab--extended .md3-fab__label {
  width: auto; /* 允许文本撑开，配合 JS 或 max-width 动画效果更好，这里简单处理 */
  min-width: 60px; /* 估算的最小文本宽度，防止动画卡顿 */
  opacity: 1;
}

/* ================== 状态层与交互 ================== */

.md3-fab__state-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: currentColor;
  opacity: 0;
  transition: opacity 0.2s;
  pointer-events: none;
}

.md3-fab:hover {
  box-shadow: var(--md-elevation-4);
}

.md3-fab:hover .md3-fab__state-layer {
  opacity: 0.08;
}

.md3-fab:active .md3-fab__state-layer {
  opacity: 0.12;
}

/* ================== 固定定位模式 ================== */
.md3-fab--fixed {
  position: absolute; /* 相对于最近的 relative 父级，或者 fixed 相对于窗口 */
  bottom: 24px;
  right: 24px;
}
</style>