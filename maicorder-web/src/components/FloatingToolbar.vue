<template>
  <div class="floating-toolbar-container">
    <div class="floating-surface" role="toolbar">
      <button v-for="(item, index) in items" :key="item.key || index" class="toolbar-item" :class="{
        'is-active': selectable && modelValue === item.key,
        'has-label': showLabel && item.label
      }" :title="item.title" @click="handleClick(item)">
        <!-- 涟漪层 -->
        <div class="ripple-overlay"></div>

        <!-- 内容层：图标 + 文字 -->
        <div class="content-wrapper">
          <!-- 图标 -->
          <div class="icon-box">
            <slot name="icon" :item="item" :isActive="modelValue === item.key">
              <!-- 方案 1：组件形式的图标 -->
              <component :is="item.icon" v-if="typeof item.icon === 'object' && item.icon !== null" />

              <!-- 方案 2：URL 形式的图标（使用 img 标签） -->
              <img v-else-if="typeof item.icon === 'string' && item.icon.includes('.svg')" :src="item.icon" alt="icon"
                class="svg-icon" />

              <!-- 方案 3：SVG 字符串形式的图标 -->
              <span v-else-if="typeof item.icon === 'string'" v-html="item.icon" class="html-icon"></span>
            </slot>
          </div>

          <!-- 文字标签 (新增功能) -->
          <span v-if="showLabel && item.label" class="label-text">
            {{ item.label }}
          </span>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

export interface ToolbarItem {
  key: string | number;
  title?: string;        // 鼠标悬停的原生提示
  label?: string;        // 新增：显示的文字标签
  icon?: any;            // 图标
  route?: string | object;
  action?: () => void;
}

const props = withDefaults(defineProps<{
  items: ToolbarItem[];
  modelValue?: string | number | null;
  // 新增：是否允许选中（显示白色背景）
  selectable?: boolean;
  // 新增：是否显示文字标签
  showLabel?: boolean;
}>(), {
  selectable: true,
  showLabel: false
});

const emit = defineEmits<{
  (e: 'update:modelValue', key: string | number): void;
  (e: 'click', item: ToolbarItem): void;
}>();

const router = useRouter();

const handleClick = (item: ToolbarItem) => {
  // 1. 路由跳转
  if (item.route && router) {
    router.push(item.route);
  }

  // 2. 回调函数
  if (item.action) {
    item.action();
  }

  // 3. 更新选中值 (仅当开启 selectable 时才有意义，但逻辑上始终更新 modelValue)
  emit('update:modelValue', item.key);
  emit('click', item);
};
</script>

<style scoped>
/* 
  Material Design 3 Token 复刻 
*/
.floating-toolbar-container {
  position: fixed;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  display: flex;
  justify-content: center;
  pointer-events: none;
  /* 容器穿透 */
}

.floating-surface {
  pointer-events: auto;
  display: inline-flex;
  align-items: center;
  background-color: #E8DEF8;
  /* Surface Container Low (浅紫) */
  border-radius: 999px;
  /* 全圆角 */
  padding: 8px;
  /* 容器内边距 */
  gap: 8px;
  /* 按钮间距 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.14), 0 1px 3px rgba(0, 0, 0, 0.12);
  transition: all 0.3s cubic-bezier(0.2, 0, 0, 1);
}

.toolbar-item {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  outline: none;
  background: transparent;
  cursor: pointer;
  color: #49454F;
  /* Inactive Icon Color */
  padding: 0;
  /* 默认无内边距，由宽高控制 */
  min-width: 44px;
  /* 最小触摸区域 */
  height: 44px;
  border-radius: 22px;
  /* 圆形或者半个高度 */
  transition: all 0.2s cubic-bezier(0.2, 0, 0, 1);
  overflow: hidden;
  user-select: none;
  -webkit-tap-highlight-color: transparent;
}

/* --- 状态样式 --- */

/* 1. 选中态 (Active) - 仅当 selectable 为 true 时生效 */
.toolbar-item.is-active {
  background-color: #FFFFFF;
  /* 选中背景：白 */
  color: #1D192B;
  /* 选中文字/图标：深紫黑 */
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08);
  font-weight: 600;
}

/* 2. 悬停态 (Hover) */
.toolbar-item:not(.is-active):hover {
  background-color: rgba(29, 25, 43, 0.08);
  color: #1D192B;
}

.toolbar-item.is-active:hover {
  background-color: #F2EFF5;
  /* 选中状态下的微弱 Hover */
}

/* --- 布局模式 --- */

.content-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

/* 纯图标模式 (默认) */
.icon-box {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
}

/* 带文字模式 (Label) */
.toolbar-item.has-label {
  padding: 0 16px 0 12px;
  /* 左侧靠近图标，右侧多留白 */
  width: auto;
  /* 宽度自适应 */
  min-width: unset;
}

.toolbar-item.has-label .content-wrapper {
  gap: 8px;
  /* 图标与文字间距 */
}

.label-text {
  font-family: 'Roboto', sans-serif;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  line-height: 1;
}

/* SVG 适配 */
:deep(svg) {
  width: 20px;
  height: 20px;
  fill: currentColor;
}

:deep(.html-icon) {
  font-size: 18px;
  line-height: 1;
}
</style>